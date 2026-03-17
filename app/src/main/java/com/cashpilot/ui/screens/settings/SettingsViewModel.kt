package com.cashpilot.ui.screens.settings

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashpilot.billing.BillingManager
import com.cashpilot.data.CashPilotRepository
import com.cashpilot.data.local.entity.PeriodType
import com.cashpilot.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferencesRepository,
    private val billingManager: BillingManager,
    private val repository: CashPilotRepository
) : ViewModel() {

    val periodType: StateFlow<PeriodType> = userPreferences.periodTypeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PeriodType.MONTHLY
        )

    val adsRemoved: StateFlow<Boolean> = userPreferences.adsRemovedFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    val purchaseResult = billingManager.purchaseResult

    /** Cambia el periodo y borra todos los datos (ingresos, gastos fijos y variables). */
    fun setPeriodTypeAndClearData(periodType: PeriodType) {
        viewModelScope.launch {
            repository.clearAllData()
            userPreferences.setPeriodType(periodType)
        }
    }

    fun launchRemoveAdsPurchase(activity: Activity) {
        billingManager.launchRemoveAdsPurchase(activity)
    }
}
