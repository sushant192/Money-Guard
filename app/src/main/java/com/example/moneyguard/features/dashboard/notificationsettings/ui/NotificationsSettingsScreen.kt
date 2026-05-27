@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.moneyguard.features.dashboard.notificationsettings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneyguard.R
import com.example.moneyguard.core.arch.BaseScreen
import com.example.moneyguard.features.dashboard.notificationsettings.domain.model.NotificationSound
import com.example.moneyguard.ui.theme.BrandBlue
import com.example.moneyguard.ui.theme.HomeHeaderBlue
import com.example.moneyguard.ui.theme.MoneyGuardTheme
import com.example.moneyguard.ui.theme.MutedText
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun NotificationsSettingsScreen(viewModel: NotificationsSettingsViewModel) {
    BaseScreen(viewModel) { state ->
        NotificationsSettingsContent(
            state = state.value,
            event = viewModel,
        )
    }
}

@Composable
private fun NotificationsSettingsContent(
    state: NotificationsSettingsUiState,
    event: NotificationsSettingsUiEvents,
) {
    if (state.showQuietHoursStartPicker) {
        QuietHoursTimePickerDialog(
            title = stringResource(R.string.notifications_pick_start_time),
            initialMinutes = state.quietHoursStartMinutes,
            onConfirm = event::onQuietHoursStartSelected,
            onDismiss = event::onDismissTimePicker,
        )
    }
    if (state.showQuietHoursEndPicker) {
        QuietHoursTimePickerDialog(
            title = stringResource(R.string.notifications_pick_end_time),
            initialMinutes = state.quietHoursEndMinutes,
            onConfirm = event::onQuietHoursEndSelected,
            onDismiss = event::onDismissTimePicker,
        )
    }

    val postNotificationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            event.onPostNotificationPromptHandled(granted)
        }

    LaunchedEffect(state.requestPostNotificationPermission) {
        if (!state.requestPostNotificationPermission) return@LaunchedEffect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            postNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            event.onPostNotificationPromptHandled(granted = true)
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(HomeHeaderBlue),
    ) {
        NotificationsSettingsHeader(onBackClick = event::onBackClick)

        Surface(
            modifier =
                Modifier
                    .fillMaxSize()
                    .weight(1f),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color.White,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding()
                        .padding(bottom = 32.dp),
            ) {
                if (state.showPermissionDeniedBanner) {
                    NotificationPermissionBanner(
                        onAllowClick = event::onRequestPostNotificationPermissionClick,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    )
                } else {
                    Spacer(Modifier.height(20.dp))
                }

                SettingsSectionHeader(stringResource(R.string.notifications_section_spending))

                SettingsGroupCard(
                    modifier = Modifier.padding(horizontal = 20.dp),
                ) {
                    NotificationToggleRow(
                        icon = Icons.Outlined.Star,
                        iconBackground = Color(0xFFFFEDD5),
                        iconTint = Color(0xFFF59E0B),
                        title = stringResource(R.string.notifications_limit_warning_title),
                        subtitle =
                            stringResource(
                                R.string.notifications_limit_warning_subtitle,
                                state.limitWarningThresholdPercent,
                            ),
                        checked = state.limitWarning,
                        onCheckedChange = event::onLimitWarningToggle,
                    )
                    SettingsRowDivider()
                    NotificationToggleRow(
                        icon = Icons.Outlined.ErrorOutline,
                        iconBackground = Color(0xFFFEE2E2),
                        iconTint = Color(0xFFDC2626),
                        title = stringResource(R.string.notifications_limit_exceeded_title),
                        subtitle = stringResource(R.string.notifications_limit_exceeded_subtitle),
                        checked = state.limitExceeded,
                        onCheckedChange = event::onLimitExceededToggle,
                    )
                    SettingsRowDivider()
                    NotificationToggleRow(
                        icon = Icons.Outlined.CheckCircle,
                        iconBackground = Color(0xFFD8F1DD),
                        iconTint = Color(0xFF2E7D32),
                        title = stringResource(R.string.notifications_daily_summary_title),
                        subtitle = stringResource(R.string.notifications_daily_summary_subtitle),
                        checked = state.dailySummary,
                        onCheckedChange = event::onDailySummaryToggle,
                    )
                }

                SettingsSectionHeader(stringResource(R.string.notifications_section_transactions))

                SettingsGroupCard(
                    modifier = Modifier.padding(horizontal = 20.dp),
                ) {
                    NotificationToggleRow(
                        icon = Icons.Outlined.CreditCard,
                        iconBackground = Color(0xFFE5EEFB),
                        iconTint = BrandBlue,
                        title = stringResource(R.string.notifications_new_expense_title),
                        subtitle = stringResource(R.string.notifications_new_expense_subtitle),
                        checked = state.newExpenseDetected,
                        onCheckedChange = event::onNewExpenseDetectedToggle,
                    )
                    SettingsRowDivider()
                    NotificationToggleRow(
                        icon = Icons.Outlined.TaskAlt,
                        iconBackground = Color(0xFFEDE9FE),
                        iconTint = Color(0xFF7C3AED),
                        title = stringResource(R.string.notifications_large_txn_title),
                        subtitle = stringResource(R.string.notifications_large_txn_subtitle),
                        checked = state.largeTransaction,
                        onCheckedChange = event::onLargeTransactionToggle,
                    )
                }

                SettingsSectionHeader(stringResource(R.string.notifications_section_sound))

                SoundSelectorRow(
                    selected = state.sound,
                    onSelected = event::onSoundSelected,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Spacer(Modifier.height(16.dp))

                QuietHoursCard(
                    quietHoursEnabled = state.quietHoursEnabled,
                    startLabel = formatMinutesAsTime(state.quietHoursStartMinutes),
                    endLabel = formatMinutesAsTime(state.quietHoursEndMinutes),
                    onQuietHoursToggle = event::onQuietHoursToggle,
                    onStartClick = event::onQuietHoursStartClick,
                    onEndClick = event::onQuietHoursEndClick,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
        }
    }
}

@Composable
private fun NotificationPermissionBanner(
    onAllowClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFE8F2FC))
                .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.notifications_permission_banner_title),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.notifications_permission_banner_body),
            fontSize = 13.sp,
            lineHeight = 20.sp,
            color = MutedText,
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onAllowClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = HomeHeaderBlue,
                    contentColor = Color.White,
                ),
            elevation = ButtonDefaults.buttonElevation(0.dp),
        ) {
            Text(
                text = stringResource(R.string.notifications_permission_banner_action),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun NotificationsSettingsHeader(onBackClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
    ) {
        IconButton(
            onClick = onBackClick,
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.notifications_settings_back),
                tint = Color.White,
            )
        }
        Text(
            text = stringResource(R.string.notifications_settings_title),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
        )
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = MutedText,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.8.sp,
        modifier =
            Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 10.dp),
    )
}

@Composable
private fun SettingsGroupCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp)),
    ) {
        content()
    }
}

@Composable
private fun SettingsRowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 1.dp,
        color = Color(0xFFEEF1F4),
    )
}

@Composable
private fun NotificationToggleRow(
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBackground),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = MutedText,
            )
        }
        NotificationSwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun NotificationSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors =
            SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = HomeHeaderBlue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD1D5DB),
            ),
    )
}

@Composable
private fun SoundSelectorRow(
    selected: NotificationSound,
    onSelected: (NotificationSound) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options =
        listOf(
            NotificationSound.DEFAULT to stringResource(R.string.notifications_sound_default),
            NotificationSound.SUBTLE to stringResource(R.string.notifications_sound_subtle),
            NotificationSound.NONE to stringResource(R.string.notifications_sound_none),
        )
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        options.forEach { (sound, label) ->
            val isSelected = selected == sound
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color.White else Color(0xFFE8F2FC))
                        .border(
                            width = if (isSelected) 1.5.dp else 0.dp,
                            color = if (isSelected) HomeHeaderBlue else Color.Transparent,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable { onSelected(sound) }
                        .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (isSelected) HomeHeaderBlue else BrandBlue,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun QuietHoursCard(
    quietHoursEnabled: Boolean,
    startLabel: String,
    endLabel: String,
    onQuietHoursToggle: (Boolean) -> Unit,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE8F2FC))
                .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.notifications_quiet_hours_title),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827),
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.notifications_quiet_hours_subtitle),
                    fontSize = 13.sp,
                    color = MutedText,
                )
            }
            NotificationSwitch(
                checked = quietHoursEnabled,
                onCheckedChange = onQuietHoursToggle,
            )
        }

        if (quietHoursEnabled) {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                QuietHoursTimeBox(
                    label = stringResource(R.string.notifications_time_from),
                    time = startLabel,
                    onClick = onStartClick,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "—",
                    color = MutedText,
                    fontSize = 16.sp,
                )
                QuietHoursTimeBox(
                    label = stringResource(R.string.notifications_time_to),
                    time = endLabel,
                    onClick = onEndClick,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun QuietHoursTimeBox(
    label: String,
    time: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, HomeHeaderBlue.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MutedText,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = time,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = HomeHeaderBlue,
        )
    }
}

@Composable
private fun QuietHoursTimePickerDialog(
    title: String,
    initialMinutes: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val hour = initialMinutes / 60
    val minute = initialMinutes % 60
    val timePickerState =
        rememberTimePickerState(
            initialHour = hour,
            initialMinute = minute,
            is24Hour = false,
        )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { TimePicker(state = timePickerState) },
        confirmButton = {
            TextButton(
                onClick = {
                    val totalMinutes = timePickerState.hour * 60 + timePickerState.minute
                    onConfirm(totalMinutes)
                },
            ) {
                Text(stringResource(R.string.notifications_time_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.notifications_time_cancel))
            }
        },
    )
}

private fun formatMinutesAsTime(totalMinutes: Int): String {
    val calendar =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, totalMinutes / 60)
            set(Calendar.MINUTE, totalMinutes % 60)
        }
    return DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(calendar.time)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NotificationsSettingsPreview() {
    MoneyGuardTheme {
        NotificationsSettingsContent(
            state = NotificationsSettingsUiState.Initial,
            event =
                object : NotificationsSettingsUiEvents {
                    override fun onBackClick() = Unit
                    override fun onLimitWarningToggle(enabled: Boolean) = Unit
                    override fun onLimitExceededToggle(enabled: Boolean) = Unit
                    override fun onDailySummaryToggle(enabled: Boolean) = Unit
                    override fun onNewExpenseDetectedToggle(enabled: Boolean) = Unit
                    override fun onLargeTransactionToggle(enabled: Boolean) = Unit
                    override fun onQuietHoursToggle(enabled: Boolean) = Unit
                    override fun onSoundSelected(sound: NotificationSound) = Unit
                    override fun onQuietHoursStartClick() = Unit
                    override fun onQuietHoursEndClick() = Unit
                    override fun onQuietHoursStartSelected(totalMinutes: Int) = Unit
                    override fun onQuietHoursEndSelected(totalMinutes: Int) = Unit
                    override fun onDismissTimePicker() = Unit
                    override fun onPostNotificationPromptHandled(granted: Boolean) = Unit
                    override fun onRequestPostNotificationPermissionClick() = Unit
                },
        )
    }
}
