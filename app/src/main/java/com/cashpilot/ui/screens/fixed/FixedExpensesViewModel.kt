package com.cashpilot.ui.screens.fixed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashpilot.data.CashPilotRepository
import com.cashpilot.data.local.entity.FixedExpenseEntity
import com.cashpilot.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FixedExpensesViewModel @Inject constructor(
    private val repository: CashPilotRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    val fixedExpenses: StateFlow<List<FixedExpenseEntity>> =
        userPreferences.periodTypeFlow
            .flatMapLatest { periodType ->
                repository.getFixedExpensesForPeriod(periodType)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addFixedExpense(
        name: String,
        amount: Double,
        category: String
    ) {
        viewModelScope.launch {
            val periodType = userPreferences.periodTypeFlow.first()
            repository.addFixedExpense(
                name = name,
                amount = amount,
                category = category,
                periodType = periodType
            )
        }
    }
}

