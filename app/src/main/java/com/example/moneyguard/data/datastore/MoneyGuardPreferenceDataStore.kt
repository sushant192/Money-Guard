package com.example.moneyguard.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single

private const val PREFERENCE_FILE_NAME = "moneyguard_prefs"

private val PREFS_KEY_HAS_COMPLETED_BUDGET_SETUP =
    booleanPreferencesKey("PREFS_KEY_HAS_COMPLETED_BUDGET_SETUP")
private val PREFS_KEY_DAILY_LIMIT = intPreferencesKey("PREFS_KEY_DAILY_LIMIT")
private val PREFS_KEY_SELECTED_CATEGORIES =
    stringSetPreferencesKey("PREFS_KEY_SELECTED_CATEGORIES")

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

    suspend fun clearBudgetSetup() {
        context.preferences.edit { prefs ->
            prefs.remove(PREFS_KEY_DAILY_LIMIT)
            prefs.remove(PREFS_KEY_SELECTED_CATEGORIES)
            prefs.remove(PREFS_KEY_HAS_COMPLETED_BUDGET_SETUP)
        }
    }
}
