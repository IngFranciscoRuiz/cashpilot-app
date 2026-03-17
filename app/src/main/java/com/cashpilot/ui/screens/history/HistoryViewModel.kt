package com.cashpilot.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashpilot.data.CashPilotRepository
import com.cashpilot.data.local.entity.ExpenseEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: CashPilotRepository
) : ViewModel() {

    private val _history = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val history: StateFlow<List<ExpenseEntity>> = _history.asStateFlow()

    init {
        viewModelScope.launch {
            val today = LocalDate.now()
            val start = today.minusMonths(1)
            repository.getVariableExpensesForRange(start, today).collect { list ->
                _history.value = list
            }
        }
    }
}

