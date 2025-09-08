package io.peng.sparrowdelivery.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import io.peng.sparrowdelivery.presentation.onboarding.UserOnboardingPrefs

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesManager(private val context: Context) {

    companion object {
        private val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        private val HAS_SETUP_LOCATION = booleanPreferencesKey("has_setup_location")
        private val PREFERRED_ADDRESS = stringPreferencesKey("preferred_address")
        private val PREFERRED_ADDRESS_LABEL = stringPreferencesKey("preferred_address_label")
        private val ONBOARDING_COMPLETED_AT = longPreferencesKey("onboarding_completed_at")
        private val USER_NAME = stringPreferencesKey("user_name")
    }

    // Get user onboarding preferences as Flow
    val userOnboardingPrefs: Flow<UserOnboardingPrefs> = context.dataStore.data
        .map { preferences ->
            UserOnboardingPrefs(
                hasCompletedOnboarding = preferences[HAS_COMPLETED_ONBOARDING] ?: false,
                hasSetupLocation = preferences[HAS_SETUP_LOCATION] ?: false,
                preferredAddress = preferences[PREFERRED_ADDRESS],
                preferredAddressLabel = preferences[PREFERRED_ADDRESS_LABEL],
                onboardingCompletedAt = preferences[ONBOARDING_COMPLETED_AT]
            )
        }

    // Get user name
    val userName: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_NAME] ?: ""
        }

    // Mark onboarding as completed
    suspend fun completeOnboarding() {
        context.dataStore.edit { preferences ->
            preferences[HAS_COMPLETED_ONBOARDING] = true
            preferences[ONBOARDING_COMPLETED_AT] = System.currentTimeMillis()
        }
    }

    // Save user location preferences
    suspend fun saveLocationPreferences(address: String, label: String) {
        context.dataStore.edit { preferences ->
            preferences[HAS_SETUP_LOCATION] = true
            preferences[PREFERRED_ADDRESS] = address
            preferences[PREFERRED_ADDRESS_LABEL] = label
        }
    }

    // Save user name
    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    // Clear all onboarding data (for testing)
    suspend fun clearOnboardingData() {
        context.dataStore.edit { preferences ->
            preferences.remove(HAS_COMPLETED_ONBOARDING)
            preferences.remove(HAS_SETUP_LOCATION)
            preferences.remove(PREFERRED_ADDRESS)
            preferences.remove(PREFERRED_ADDRESS_LABEL)
            preferences.remove(ONBOARDING_COMPLETED_AT)
        }
    }

    // Reset everything (for testing)
    suspend fun clearAllPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
