package com.example.moneyguard.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    indices = [
        Index(value = ["createdAtEpochMs"]),
        Index(value = ["sourceKey"], unique = true),
        Index(value = ["listenerNotificationKey"], unique = true),
    ],
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amountRupees: Int,
    val note: String,
    /** [com.example.moneyguard.features.dashboard.home.ui.ExpenseIconStyle.name] */
    val category: String,
    val paymentSource: String,
    val createdAtEpochMs: Long,
    /** Semantic dedupe key — null for manual entries. */
    val sourceKey: String? = null,
    /** [android.service.notification.StatusBarNotification.getKey] — blocks repeat callbacks for one shade entry. */
    val listenerNotificationKey: String? = null,
)
