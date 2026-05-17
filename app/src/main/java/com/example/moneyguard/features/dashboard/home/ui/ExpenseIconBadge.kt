package com.example.moneyguard.features.dashboard.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/** Leading icon on Home / History expense rows. */
@Composable
fun ExpenseIconBadge(
    style: ExpenseIconStyle,
    useUpiPaymentIcon: Boolean = false,
) {
    if (useUpiPaymentIcon || style == ExpenseIconStyle.Transfer) {
        UpiPaymentIconBadge()
        return
    }

    val bg: Color
    val tint: Color
    val icon: ImageVector
    when (style) {
        ExpenseIconStyle.Entertainment -> {
            bg = Color(0xFFE9DEFD)
            tint = Color(0xFF7C4DFF)
            icon = Icons.Outlined.PlayCircle
        }
        ExpenseIconStyle.Food -> {
            bg = Color(0xFFFFE6C9)
            tint = Color(0xFFE67E22)
            icon = Icons.Outlined.Restaurant
        }
        ExpenseIconStyle.Bills -> {
            bg = Color(0xFFE5EEFB)
            tint = Color(0xFF2563EB)
            icon = Icons.Outlined.CreditCard
        }
        ExpenseIconStyle.Travel -> {
            bg = Color(0xFFFFE4EC)
            tint = Color(0xFFE91E63)
            icon = Icons.Outlined.LocationOn
        }
        ExpenseIconStyle.Transfer -> error("handled above")
    }
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
    }
}

/** Mint tile + green circle + white arrow — UPI / transfer payments. */
@Composable
fun UpiPaymentIconBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFD8F1DD)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFF2E7D32)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
