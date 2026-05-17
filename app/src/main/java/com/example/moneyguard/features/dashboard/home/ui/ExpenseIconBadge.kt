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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Leading icon on Home / History expense rows and the detail sheet header. */
@Composable
fun ExpenseIconBadge(
    style: ExpenseIconStyle,
    useUpiPaymentIcon: Boolean = false,
    tileSize: Dp = 44.dp,
) {
    if (useUpiPaymentIcon) {
        UpiPaymentIconBadge(tileSize = tileSize)
        return
    }

    val iconSize = tileSize * 0.5f
    val corner = tileSize * 0.27f

    val bg: Color
    val tint: Color
    val icon: ImageVector
    when (style) {
        ExpenseIconStyle.Transfer -> {
            UpiPaymentIconBadge(tileSize = tileSize)
            return
        }
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
    }
    Box(
        modifier = Modifier
            .size(tileSize)
            .clip(RoundedCornerShape(corner))
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(iconSize),
        )
    }
}

/** Mint tile + green circle + white arrow — UPI / transfer payments. */
@Composable
fun UpiPaymentIconBadge(
    modifier: Modifier = Modifier,
    tileSize: Dp = 44.dp,
) {
    val innerCircle = tileSize * 0.64f
    val arrowSize = tileSize * 0.36f
    val corner = tileSize * 0.27f
    Box(
        modifier = modifier
            .size(tileSize)
            .clip(RoundedCornerShape(corner))
            .background(Color(0xFFD8F1DD)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(innerCircle)
                .clip(CircleShape)
                .background(Color(0xFF2E7D32)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(arrowSize),
            )
        }
    }
}
