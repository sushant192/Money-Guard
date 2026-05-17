package com.example.moneyguard.features.dashboard.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.moneyguard.R
import com.example.moneyguard.ui.theme.BrandBlue
import com.example.moneyguard.ui.theme.ErrorMain
import com.example.moneyguard.ui.theme.FieldBackground
import com.example.moneyguard.ui.theme.MutedText
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ExpenseDetailBottomSheet(
    detail: ExpenseDetailUi,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
) {
    val scrimInteraction = remember { MutableInteractionSource() }
    val nf = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")) }

    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
            ),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = scrimInteraction,
                        indication = null,
                        onClick = onDismiss,
                    ),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Surface(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        ),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White,
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp)
                            .padding(top = 10.dp, bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFE5E7EB)),
                    )
                    Spacer(Modifier.height(20.dp))

                    ExpenseIconBadge(
                        style = detail.iconStyle,
                        tileSize = 90.dp,
                    )

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = detail.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = detail.metaLine,
                        fontSize = 14.sp,
                        color = MutedText,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "−₹${nf.format(detail.amountRupees)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = ErrorMain,
                    )

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        DetailInfoTile(
                            label = stringResource(R.string.expense_detail_date),
                            value = detail.dateLabel,
                            modifier = Modifier.weight(1f),
                        )
                        DetailInfoTile(
                            label = stringResource(R.string.expense_detail_time),
                            value = detail.timeLabel,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        DetailInfoTile(
                            label = stringResource(R.string.expense_detail_category),
                            value = detail.categoryLabel,
                            valueColor = detail.categoryAccent,
                            modifier = Modifier.weight(1f),
                        )
                        DetailInfoTile(
                            label = stringResource(R.string.expense_detail_method),
                            value = detail.paymentLabel,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    DetailInfoTile(
                        label = stringResource(R.string.expense_detail_note),
                        value =
                            detail.note.ifBlank {
                                stringResource(R.string.expense_detail_note_empty)
                            },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(24.dp))
                    HorizontalDivider(color = Color(0xFFF3F4F6))
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DetailActionChip(
                            label = stringResource(R.string.expense_detail_delete),
                            icon = Icons.Outlined.Close,
                            tint = ErrorMain,
                            background = Color(0xFFFFEBEE),
                            onClick = onDelete,
                            modifier = Modifier.weight(1f),
                        )
                        DetailActionChip(
                            label = stringResource(R.string.expense_detail_edit),
                            icon = Icons.Outlined.Edit,
                            tint = BrandBlue,
                            background = FieldBackground,
                            onClick = onEdit,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailInfoTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color(0xFF111827),
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(14.dp))
                .background(FieldBackground)
                .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MutedText,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
        )
    }
}

@Composable
private fun DetailActionChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    background: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .height(60.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(background)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = tint,
        )
    }
}
