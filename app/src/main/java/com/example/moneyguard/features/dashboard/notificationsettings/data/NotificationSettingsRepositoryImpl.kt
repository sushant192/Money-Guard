package com.example.moneyguard.features.dashboard.notificationsettings.data

import com.example.moneyguard.data.datastore.MoneyGuardPreferenceDataStore
import com.example.moneyguard.features.dashboard.notificationsettings.domain.model.NotificationSettings
import com.example.moneyguard.features.dashboard.notificationsettings.domain.repository.NotificationSettingsRepository
import kotlinx.coroutines.flow.Flow

class NotificationSettingsRepositoryImpl(
    private val preferenceDataStore: MoneyGuardPreferenceDataStore,
) : NotificationSettingsRepository {

    override fun observeSettings(): Flow<NotificationSettings> =
        preferenceDataStore.observeNotificationSettings()

    override suspend fun saveSettings(settings: NotificationSettings) {
        preferenceDataStore.saveNotificationSettings(settings)
    }
}
