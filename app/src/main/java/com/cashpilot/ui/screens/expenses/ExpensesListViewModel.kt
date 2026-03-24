package com.cashpilot.ui.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashpilot.data.CashPilotRepository
import com.cashpilot.data.local.entity.ExpenseEntity
import com.cashpilot.data.local.entity.FixedExpenseEntity
import com.cashpilot.data.preferences.UserPreferencesRepository
import com.cashpilot.util.currentCalendarMonthRange
import com.cashpilot.util.expenseDaySectionTitle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

data class ExpenseDayGroup(
    val keyDate: LocalDate,
    val headerLabel: String,
    val expenses: List<ExpenseEntity>,
    val dayTotal: Double
)

data class ExpensesListUiState(
    val fixedExpenses: List<FixedExpenseEntity> = emptyList(),
    val totalFixed: Double = 0.0,
    /** Gastos variables del mes actual, agrupados por día (días más recientes primero). */
    val variableDayGroups: List<ExpenseDayGroup> = emptyList(),
    val totalVariableMonth: Double = 0.0,
    val remaining: Double = 0.0
)

private fun List<ExpenseEntity>.toVariableDayGroups(today: LocalDate): List<ExpenseDayGroup> {
    val byDate = LinkedHashMap<LocalDate, MutableList<ExpenseEntity>>()
    for (e in this) {
        byDate.getOrPut(e.date) { mutableListOf() }.add(e)
    }
    return byDate.keys.sortedDescending().map { date ->
        val list = byDate.getValue(date)
        ExpenseDayGroup(
            keyDate = date,
            headerLabel = expenseDaySectionTitle(date, today),
            expenses = list,
            dayTotal = list.sumOf { it.amount }
        )
    }
}

@HiltViewModel
class ExpensesListViewModel @Inject constructor(
    private val repository: CashPilotRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<ExpensesListUiState> =
        userPreferences.periodTypeFlow
            .flatMapLatest { periodType ->
                val monthRange = currentCalendarMonthRange()
                val monthStart = monthRange.first
                val monthEnd = monthRange.second
                val today = LocalDate.now()
                combine(
                    repository.getFixedExpensesForPeriod(periodType),
                    repository.getTotalFixedForPeriod(periodType).map { it ?: 0.0 },
                    repository.getVariableExpensesForRange(monthStart, monthEnd),
                    repository.getTotalVariableForRange(monthStart, monthEnd).map { it ?: 0.0 },
                    repository.getTotalIncomeInDateRange(monthStart, monthEnd).map { it ?: 0.0 }
                ) { fixedList, totalFixed, variableList, totalVariable, income ->
                    ExpensesListUiState(
                        fixedExpenses = fixedList,
                        totalFixed = totalFixed,
                        variableDayGroups = variableList.toVariableDayGroups(today),
                        totalVariableMonth = totalVariable,
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
