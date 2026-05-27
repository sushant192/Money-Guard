package com.example.moneyguard.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.moneyguard.features.dashboard.notificationsettings.domain.model.NotificationSettings
import com.example.moneyguard.features.dashboard.notificationsettings.domain.model.NotificationSound
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

private const val PREFERENCE_FILE_NAME = "moneyguard_prefs"

private val PREFS_KEY_HAS_COMPLETED_BUDGET_SETUP =
    booleanPreferencesKey("PREFS_KEY_HAS_COMPLETED_BUDGET_SETUP")
private val PREFS_KEY_DAILY_LIMIT = intPreferencesKey("PREFS_KEY_DAILY_LIMIT")
private val PREFS_KEY_SELECTED_CATEGORIES =
    stringSetPreferencesKey("PREFS_KEY_SELECTED_CATEGORIES")

private val PREFS_KEY_NOTIF_LIMIT_WARNING = booleanPreferencesKey("PREFS_KEY_NOTIF_LIMIT_WARNING")
private val PREFS_KEY_NOTIF_LIMIT_EXCEEDED = booleanPreferencesKey("PREFS_KEY_NOTIF_LIMIT_EXCEEDED")
private val PREFS_KEY_NOTIF_DAILY_SUMMARY = booleanPreferencesKey("PREFS_KEY_NOTIF_DAILY_SUMMARY")
private val PREFS_KEY_NOTIF_NEW_EXPENSE = booleanPreferencesKey("PREFS_KEY_NOTIF_NEW_EXPENSE")
private val PREFS_KEY_NOTIF_LARGE_TXN = booleanPreferencesKey("PREFS_KEY_NOTIF_LARGE_TXN")
private val PREFS_KEY_NOTIF_QUIET_HOURS = booleanPreferencesKey("PREFS_KEY_NOTIF_QUIET_HOURS")
private val PREFS_KEY_NOTIF_SOUND = stringPreferencesKey("PREFS_KEY_NOTIF_SOUND")
private val PREFS_KEY_NOTIF_QUIET_START = intPreferencesKey("PREFS_KEY_NOTIF_QUIET_START")
private val PREFS_KEY_NOTIF_QUIET_END = intPreferencesKey("PREFS_KEY_NOTIF_QUIET_END")

private val Context.preferences by preferencesDataStore(name = PREFERENCE_FILE_NAME)

/**
 * Lightweight local persistence for the one-time post-auth onboarding setup.
 *
 * Capricorn uses Preferences DataStore for small key/value user state; we do
 * the same here because a daily limit + selected categories do not justify a
 * Room table yet.
 */
@Single
class MoneyGuardPreferenceDataStore(
    private val context: Context,
) {
    suspend fun saveBudgetSetup(
        limit: Int,
        selectedCategories: Set<String>,
    ) {
        context.preferences.edit { prefs ->
            prefs[PREFS_KEY_DAILY_LIMIT] = limit
            prefs[PREFS_KEY_SELECTED_CATEGORIES] = selectedCategories
            prefs[PREFS_KEY_HAS_COMPLETED_BUDGET_SETUP] = true
        }
    }

    suspend fun hasCompletedBudgetSetup(): Boolean =
        context.preferences.data.first()[PREFS_KEY_HAS_COMPLETED_BUDGET_SETUP] ?: false

    suspend fun getDailyLimit(): Int? =
        context.preferences.data.first()[PREFS_KEY_DAILY_LIMIT]

    suspend fun clearBudgetSetup() {
        context.preferences.edit { prefs ->
            prefs.remove(PREFS_KEY_DAILY_LIMIT)
            prefs.remove(PREFS_KEY_SELECTED_CATEGORIES)
            prefs.remove(PREFS_KEY_HAS_COMPLETED_BUDGET_SETUP)
        }
    }

    fun observeNotificationSettings(): Flow<NotificationSettings> =
        context.preferences.data.map { prefs -> prefs.toNotificationSettings() }

    suspend fun saveNotificationSettings(settings: NotificationSettings) {
        context.preferences.edit { prefs ->
            prefs[PREFS_KEY_NOTIF_LIMIT_WARNING] = settings.limitWarning
            prefs[PREFS_KEY_NOTIF_LIMIT_EXCEEDED] = settings.limitExceeded
            prefs[PREFS_KEY_NOTIF_DAILY_SUMMARY] = settings.dailySummary
            prefs[PREFS_KEY_NOTIF_NEW_EXPENSE] = settings.newExpenseDetected
            prefs[PREFS_KEY_NOTIF_LARGE_TXN] = settings.largeTransaction
            prefs[PREFS_KEY_NOTIF_QUIET_HOURS] = settings.quietHoursEnabled
            prefs[PREFS_KEY_NOTIF_SOUND] = settings.sound.name
            prefs[PREFS_KEY_NOTIF_QUIET_START] = settings.quietHoursStartMinutes
            prefs[PREFS_KEY_NOTIF_QUIET_END] = settings.quietHoursEndMinutes
        }
    }

    private fun androidx.datastore.preferences.core.Preferences.toNotificationSettings(): NotificationSettings {
        val soundName = this[PREFS_KEY_NOTIF_SOUND]
        val sound =
            soundName?.let { runCatching { NotificationSound.valueOf(it) }.getOrNull() }
                ?: NotificationSound.DEFAULT
        return NotificationSettings(
            limitWarning = this[PREFS_KEY_NOTIF_LIMIT_WARNING] ?: true,
            limitExceeded = this[PREFS_KEY_NOTIF_LIMIT_EXCEEDED] ?: true,
            dailySummary = this[PREFS_KEY_NOTIF_DAILY_SUMMARY] ?: true,
            newExpenseDetected = this[PREFS_KEY_NOTIF_NEW_EXPENSE] ?: true,
            largeTransaction = this[PREFS_KEY_NOTIF_LARGE_TXN] ?: false,
            quietHoursEnabled = this[PREFS_KEY_NOTIF_QUIET_HOURS] ?: true,
            sound = sound,
            quietHoursStartMinutes = this[PREFS_KEY_NOTIF_QUIET_START] ?: 22 * 60,
            quietHoursEndMinutes = this[PREFS_KEY_NOTIF_QUIET_END] ?: 7 * 60,
        )
    }
}
