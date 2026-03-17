package com.cashpilot.ui.screens.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashpilot.data.CashPilotRepository
import com.cashpilot.data.local.entity.IncomeEntity
import com.cashpilot.data.preferences.UserPreferencesRepository
import com.cashpilot.util.currentPeriodRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class IncomeListUiState(
    val items: List<IncomeEntity> = emptyList(),
    val totalDisponible: Double = 0.0
)

@HiltViewModel
class IncomeListViewModel @Inject constructor(
    private val repository: CashPilotRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<IncomeListUiState> =
        userPreferences.periodTypeFlow
            .flatMapLatest { periodType ->
                val range = currentPeriodRange(periodType)
                repository.getIncomeForPeriod(periodType, range.first, range.second)
                    .catch { emit(emptyList()) }
                    .map { list ->
                        IncomeListUiState(
                            items = list,
                            totalDisponible = list.sumOf { it.amount }
                        )
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = IncomeListUiState()
            )
}
