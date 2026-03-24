package com.cashpilot.ui.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashpilot.data.CashPilotRepository
import com.cashpilot.data.local.entity.ExpenseCategory
import com.cashpilot.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import com.cashpilot.ui.util.parsePositiveMoneyOrNull
import com.cashpilot.ui.util.sanitizeMoneyAmountInput
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val repository: CashPilotRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    /** Nombres de gastos anteriores para sugerir al registrar (mismo nombre = mejor agrupado). */
    val expenseNameSuggestions: StateFlow<List<String>> =
        userPreferences.periodTypeFlow
            .flatMapLatest { periodType ->
                repository.getExpenseNamesForSuggestions(periodType)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun parseQuickInput(raw: String): Pair<String, String> {
        val parts = raw.trim().split(" ")
        if (parts.isEmpty()) return "" to ""

        val amountPart = parts.firstOrNull()?.let { sanitizeMoneyAmountInput(it) } ?: ""
        val descPart = parts.drop(1).joinToString(" ").ifBlank { "Gasto" }

        return amountPart to descPart
    }

    fun addQuickExpense(rawText: String) {
        val parts = rawText.trim().split(" ")
        if (parts.isEmpty()) return

        val amount = parts.firstOrNull()?.let { parsePositiveMoneyOrNull(it) } ?: return
        val desc = parts.drop(1).joinToString(" ").ifBlank { "Gasto" }

        val category = inferCategory(desc)

        viewModelScope.launch {
            repository.addVariableExpense(
                name = desc,
                amount = amount,
                category = category,
                date = LocalDate.now(),
                notes = null,
                sortOrderMillis = System.currentTimeMillis()
            )
        }
    }

    /** Guarda el gasto variable y se puede esperar desde la UI antes de cerrar. */
    suspend fun saveVariableExpense(
        name: String,
        amount: Double,
        category: ExpenseCategory = ExpenseCategory.OTHER,
        notes: String? = null,
        date: LocalDate = LocalDate.now()
    ) {
        repository.addVariableExpense(
            name = name.ifBlank { "Gasto" },
            amount = amount,
            category = category,
            date = date,
            notes = notes,
            sortOrderMillis = System.currentTimeMillis()
        )
    }

    fun inferCategory(desc: String): ExpenseCategory {
        val lower = desc.lowercase()
        return when {
            listOf("coffee", "comida", "food", "lunch", "dinner", "desayuno", "burger", "pizza").any { lower.contains(it) } ->
                ExpenseCategory.FOOD
            listOf("uber", "taxi", "bus", "metro", "gasolina", "fuel", "gas").any { lower.contains(it) } ->
                ExpenseCategory.TRANSPORT
            listOf("netflix", "spotify", "suscripción", "subscription", "hbo", "prime").any { lower.contains(it) } ->
                ExpenseCategory.BILLS
            listOf("ropa", "clothes", "nike", "zara", "shopping").any { lower.contains(it) } ->
                ExpenseCategory.SHOPPING
            listOf("cine", "movie", "beer", "bar", "fiesta").any { lower.contains(it) } ->
                ExpenseCategory.ENTERTAINMENT
            else -> ExpenseCategory.OTHER
        }
    }
}

