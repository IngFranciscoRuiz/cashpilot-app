package com.cashpilot.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashpilot.data.CashPilotRepository
import com.cashpilot.data.local.entity.PeriodType
import com.cashpilot.data.preferences.UserPreferencesRepository
import com.cashpilot.util.currentCalendarMonthRange
import com.cashpilot.ui.util.formatPesosMx
import com.cashpilot.util.formatSpanishMonthYear
import com.cashpilot.util.groupExpensesByName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class StatsUiState(
    val periodLabel: String = "",
    val dineroDelMes: Double = 0.0,
    val gastosFijos: Double = 0.0,
    val gastosVariables: Double = 0.0,
    val dineroRestante: Double = 0.0,
    /** Gastos por nombre (fijos + variables) para el donut; top 10 + "Otros" */
    val expenseByNameTotals: List<Pair<String, Double>> = emptyList(),
    val insight: String = "",
    val insightSecondary: String = ""
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: CashPilotRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<StatsUiState> =
        userPreferences.periodTypeFlow
            .flatMapLatest { periodType ->
                val monthRange = currentCalendarMonthRange()
                val monthStart = monthRange.first
                val monthEnd = monthRange.second
                combine(
                    repository.getTotalIncomeInDateRange(monthStart, monthEnd).map { it ?: 0.0 },
                    repository.getTotalFixedForPeriod(periodType).map { it ?: 0.0 },
                    repository.getTotalVariableForRange(monthStart, monthEnd).map { it ?: 0.0 },
                    repository.getFixedExpensesForPeriod(periodType),
                    repository.getVariableExpensesForRange(monthStart, monthEnd)
                ) { income, fixed, variable, fixedList, variableList ->
                    val allByName = (fixedList.map { it.name to it.amount } + variableList.map { it.name to it.amount })
                        .filter { it.first.isNotBlank() && it.second.isFinite() }
                    val byName = groupExpensesByName(allByName)
                    val top10 = byName.take(10)
                    val restSum = byName.drop(10).sumOf { it.second }
                    val expenseByNameTotals = if (restSum > 0) top10 + ("Otros" to restSum) else top10

                    val totalGastos = fixed + variable
                    val remaining = income - totalGastos
                    val top = expenseByNameTotals.maxByOrNull { it.second }
                    val insightText = if (top != null && top.second > 0) {
                        val name = top.first
                        val half = top.second / 2
                        "Gastaste ${formatPesosMx(top.second, decimals = false)} en $name este mes. Si reduces a la mitad ahorrarías ${formatPesosMx(half, decimals = false)}."
                    } else ""
                    val insight2 = when {
                        income <= 0 -> "Registra tus ingresos en la pestaña Ingresos para ver el resumen completo."
                        expenseByNameTotals.isEmpty() -> "Registra gastos (fijos y variables) para ver el desglose por nombre."
                        remaining < 0 -> "Has superado tus ingresos este mes. Revisa gastos variables para ajustar."
                        remaining > 0 && totalGastos > 0 -> "Te quedan ${formatPesosMx(remaining, decimals = false)} disponibles este mes."
                        else -> ""
                    }
                    StatsUiState(
                        periodLabel = formatSpanishMonthYear(monthStart),
                        dineroDelMes = income,
                        gastosFijos = fixed,
                        gastosVariables = variable,
                        dineroRestante = remaining,
                        expenseByNameTotals = expenseByNameTotals,
                        insight = insightText,
                        insightSecondary = insight2
                    )
                }
            }
            .catch { _ ->
                emit(StatsUiState())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = StatsUiState()
            )
}

