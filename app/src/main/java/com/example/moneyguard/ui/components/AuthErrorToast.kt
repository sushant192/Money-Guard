package com.example.moneyguard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * [SnackbarVisuals] that carries a separate title and message so the snackbar
 * host can render our two-line [AuthErrorToast] instead of the default M3
 * snackbar. Hand the title/message string-res IDs in and resolve them inside
 * the composable so we can survive locale changes.
 */
data class AuthErrorVisuals(
    val titleRes: Int,
    val messageRes: Int,
) : SnackbarVisuals {
    override val message: String = "" // unused; we render from the res IDs
    override val actionLabel: String? = null
    override val withDismissAction: Boolean = false
    override val duration: SnackbarDuration = SnackbarDuration.Short
}

/**
 * Custom snackbar body matching the design: pale red card, red filled-circle
 * "!" icon, bold dark-red title + softer body line.
 *
 * Pass through `data` so the host can dispatch dismissals; we read the two
 * string resources off the underlying [AuthErrorVisuals].
 */
@Composable
fun AuthErrorToast(data: SnackbarData) {
    val visuals = data.visuals as? AuthErrorVisuals ?: return
    AuthErrorToastBody(
        titleRes = visuals.titleRes,
        messageRes = visuals.messageRes,
    )
}

@Composable
private fun AuthErrorToastBody(
    titleRes: Int,
    messageRes: Int,
) {
    val cardBg = Color(0xFFFCE4E4)
    val iconBg = Color(0xFFDC2626)
    val titleColor = Color(0xFFB91C1C)
    val bodyColor = Color(0xFFB91C1C).copy(alpha = 0.85f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.PriorityHigh,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(titleRes),
                color = titleColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.size(2.dp))
            Text(
                text = stringResource(messageRes),
                color = bodyColor,
                fontSize = 14.sp,
                lineHeight = 18.sp,
            )
        }
    }
}
