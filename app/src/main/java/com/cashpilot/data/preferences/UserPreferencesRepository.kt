package com.cashpilot.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cashpilot.data.local.entity.PeriodType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

private val KEY_PERIOD_TYPE = stringPreferencesKey("period_type")
private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
private val KEY_ADS_REMOVED = booleanPreferencesKey("ads_removed")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val dataStore = context.dataStore

    val periodTypeFlow: Flow<PeriodType> = dataStore.data.map { prefs ->
        val value = prefs[KEY_PERIOD_TYPE] ?: PeriodType.MONTHLY.name
        try {
            PeriodType.valueOf(value)
        } catch (_: Exception) {
            PeriodType.MONTHLY
        }
    }

    suspend fun setPeriodType(periodType: PeriodType) {
        dataStore.edit { prefs ->
            prefs[KEY_PERIOD_TYPE] = periodType.name
        }
    }

    val onboardingCompletedFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    val adsRemovedFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_ADS_REMOVED] ?: false
    }

    suspend fun setAdsRemoved(removed: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_ADS_REMOVED] = removed
        }
    }
}
