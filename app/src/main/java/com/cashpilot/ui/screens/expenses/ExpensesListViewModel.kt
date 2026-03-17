package com.cashpilot.ui.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashpilot.data.CashPilotRepository
import com.cashpilot.data.local.entity.ExpenseEntity
import com.cashpilot.data.local.entity.FixedExpenseEntity
import com.cashpilot.data.preferences.UserPreferencesRepository
import com.cashpilot.util.currentPeriodRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

data class ExpensesListUiState(
    val fixedExpenses: List<FixedExpenseEntity> = emptyList(),
    val totalFixed: Double = 0.0,
    val todayExpenses: List<ExpenseEntity> = emptyList(),
    val totalVariable: Double = 0.0,
    val remaining: Double = 0.0
)

@HiltViewModel
class ExpensesListViewModel @Inject constructor(
    private val repository: CashPilotRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    private val today = LocalDate.now()

    val uiState: StateFlow<ExpensesListUiState> =
        userPreferences.periodTypeFlow
            .flatMapLatest { periodType ->
                val periodRange = currentPeriodRange(periodType)
                combine(
                    repository.getFixedExpensesForPeriod(periodType),
                    repository.getTotalFixedForPeriod(periodType).map { it ?: 0.0 },
                    repository.getVariableExpensesForRange(today, today),
                    repository.getTotalVariableForRange(periodRange.first, periodRange.second).map { it ?: 0.0 },
                    repository.getTotalIncomeForPeriod(periodType, periodRange.first, periodRange.second).map { it ?: 0.0 }
                ) { fixedList, totalFixed, todayList, totalVariable, income ->
                    ExpensesListUiState(
                        fixedExpenses = fixedList,
                        totalFixed = totalFixed,
                        todayExpenses = todayList,
                        totalVariable = totalVariable,
                        remaining = income - totalFixed - totalVariable
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ExpensesListUiState()
            )
}
