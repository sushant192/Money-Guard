package com.example.moneyguard.features.dashboard.notificationsettings.domain.repository

import com.example.moneyguard.features.dashboard.notificationsettings.domain.model.NotificationSettings
import kotlinx.coroutines.flow.Flow

interface NotificationSettingsRepository {
    fun observeSettings(): Flow<NotificationSettings>

    suspend fun saveSettings(settings: NotificationSettings)
}
