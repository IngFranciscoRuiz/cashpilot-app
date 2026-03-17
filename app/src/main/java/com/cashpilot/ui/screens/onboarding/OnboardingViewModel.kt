package com.cashpilot.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashpilot.data.local.entity.PeriodType
import com.cashpilot.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(PeriodType.MONTHLY)
    val selectedPeriod: StateFlow<PeriodType> = _selectedPeriod.asStateFlow()

    fun setSelectedPeriod(periodType: PeriodType) {
        _selectedPeriod.value = periodType
    }

    /** Guarda el periodo elegido, marca onboarding completado y ejecuta [onSaved] (para navegar). */
    fun savePeriodAndContinue(onSaved: () -> Unit) {
        viewModelScope.launch {
            userPreferences.setPeriodType(_selectedPeriod.value)
            userPreferences.setOnboardingCompleted(true)
            onSaved()
        }
    }
}
