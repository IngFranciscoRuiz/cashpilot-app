package com.cashpilot.ui.screens.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashpilot.data.CashPilotRepository
import com.cashpilot.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val repository: CashPilotRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    fun addIncome(name: String, amount: Double) {
        viewModelScope.launch {
            val periodType = userPreferences.periodTypeFlow.first()
            repository.addIncome(
                name = name,
                amount = amount,
                date = LocalDate.now(),
                periodType = periodType
            )
        }
    }
}

