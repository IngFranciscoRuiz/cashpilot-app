package com.cashpilot.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashpilot.data.CashPilotRepository
import com.cashpilot.data.local.entity.PeriodType
import com.cashpilot.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.cashpilot.util.currentPeriodRange
import com.cashpilot.util.groupExpensesByName
import java.time.LocalDate
import javax.inject.Inject

data class DashboardUiState(
    val periodType: PeriodType = PeriodType.MONTHLY,
    val totalIncome: Double = 0.0,
    val totalFixed: Double = 0.0,
    val totalVariable: Double = 0.0,
    val remaining: Double = 0.0,
    val daysLeft: Int = 0,
    val dailyRecommendation: Double = 0.0,
    /** Principales gastos del periodo por nombre (fijos + variables), ordenados por monto, top 7 */
    val topExpensesByName: List<Pair<String, Double>> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: CashPilotRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    private val period = userPreferences.periodTypeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PeriodType.MONTHLY
    )
    val periodType: StateFlow<PeriodType> = period

    val uiState: StateFlow<DashboardUiState> =
        period
            .flatMapLatest { periodType ->
                val range = currentPeriodRange(periodType)
                val start = range.first
                val end = range.second

                val incomeFlow = repository
                    .getTotalIncomeForPeriod(periodType, start, end)
                    .map { it ?: 0.0 }

                val fixedFlow = repository
                    .getTotalFixedForPeriod(periodType)
                    .map { it ?: 0.0 }

                val variableFlow = repository
                    .getTotalVariableForRange(start, end)
                    .map { it ?: 0.0 }

                val fixedListFlow = repository.getFixedExpensesForPeriod(periodType)
                val variableListFlow = repository.getVariableExpensesForRange(start, end)

                combine(
                    incomeFlow,
                    fixedFlow,
                    variableFlow,
                    fixedListFlow,
                    variableListFlow
                ) { income, fixed, variable, fixedList, variableList ->
                    val allByName = fixedList.map { it.name to it.amount } + variableList.map { it.name to it.amount }
                    val byName = groupExpensesByName(allByName)
                    val topExpensesByName = byName.take(7)

                    val today = LocalDate.now()
                    val daysLeft = (end.toEpochDay() - today.toEpochDay()).toInt().coerceAtLeast(1)
                    val remaining = income - fixed - variable
                    val daily = if (remaining > 0) remaining / daysLeft else 0.0

                    DashboardUiState(
                        periodType = periodType,
                        totalIncome = income,
                        totalFixed = fixed,
                        totalVariable = variable,
                        remaining = remaining,
                        daysLeft = daysLeft,
                        dailyRecommendation = daily,
                        topExpensesByName = topExpensesByName
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DashboardUiState()
            )

    fun setPeriod(periodType: PeriodType) {
        viewModelScope.launch {
            userPreferences.setPeriodType(periodType)
        }
    }
}

