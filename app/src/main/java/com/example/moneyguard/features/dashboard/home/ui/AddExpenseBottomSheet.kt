package com.example.moneyguard.features.dashboard.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.moneyguard.R
import com.example.moneyguard.ui.theme.BrandBlue
import com.example.moneyguard.ui.theme.FieldBackground
import com.example.moneyguard.ui.theme.MutedText
import kotlinx.coroutines.delay

private val SheetCategories = listOf(
    ExpenseIconStyle.Entertainment,
    ExpenseIconStyle.Food,
    ExpenseIconStyle.Transfer,
    ExpenseIconStyle.Bills,
    ExpenseIconStyle.Travel,
)

private val KeypadStripBackground = Color(0xFFF9FAFB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseBottomSheet(
    onDismiss: () -> Unit,
    onSave: (
        amountRupees: Int,
        title: String,
        note: String,
        category: ExpenseIconStyle,
    ) -> Unit,
) {
    var amountRaw by remember { mutableStateOf("") }
    var titleText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExpenseIconStyle.Entertainment) }
    var cursorVisible by remember { mutableStateOf(true) }

    val scrimInteraction = remember { MutableInteractionSource() }
    val categoryScrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            cursorVisible = !cursorVisible
        }
    }

    val amountInt = remember(amountRaw) { parseAmountToRupeesInt(amountRaw) }
    val canSave = amountInt != null && amountInt > 0

    val density = LocalDensity.current
    val imeBottomPx = WindowInsets.ime.getBottom(density)
    val imeVisible = imeBottomPx > 0
    val showNumericKeypad = !imeVisible

    // Material3 ModalBottomSheet uses an internal window that leaves a visible strip
    // above the gesture nav bar under edge-to-edge. A full-screen Dialog + bottom-
    // anchored Surface matches the real window height and removes that gap.
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.38f))
                    .clickable(
                        interactionSource = scrimInteraction,
                        indication = null,
                        onClick = onDismiss,
                    ),
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .align(Alignment.BottomCenter)
                    .imePadding(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                tonalElevation = 0.dp,
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        BottomSheetDefaults.DragHandle()
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.add_expense_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827),
                        )
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF3F4F6)),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = stringResource(R.string.add_expense_close_cd),
                                tint = Color(0xFF374151),
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(FieldBackground)
                                    .padding(horizontal = 18.dp, vertical = 22.dp),
                            ) {
                                Text(
                                    text = "₹",
                                    color = BrandBlue,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(end = 6.dp, bottom = 4.dp),
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    val displayAmount =
                                        if (amountRaw.isEmpty()) "0" else amountRaw
                                    Text(
                                        text = displayAmount,
                                        color = Color(0xFF111827),
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.5).sp,
                                    )
                                    Text(
                                        text = if (cursorVisible) "|" else "",
                                        color = BrandBlue,
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Light,
                                        modifier = Modifier.padding(start = 2.dp),
                                    )
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            SoftLabeledField(
                                label = stringResource(R.string.add_expense_field_title_label),
                                placeholder = stringResource(R.string.add_expense_field_title_placeholder),
                                value = titleText,
                                onValueChange = { titleText = it },
                            )

                            Spacer(Modifier.height(18.dp))

                            Text(
                                text = stringResource(R.string.add_expense_category_heading),
                                style = MaterialTheme.typography.labelMedium,
                                color = MutedText,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.2.sp,
                            )
                            Spacer(Modifier.height(10.dp))

                            Box(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.horizontalScroll(categoryScrollState),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    SheetCategories.forEach { cat ->
                                        CategoryChip(
                                            category = cat,
                                            selected = cat == selectedCategory,
                                            onClick = { selectedCategory = cat },
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            SoftLabeledField(
                                label = stringResource(R.string.add_expense_field_note_label),
                                placeholder = stringResource(R.string.add_expense_field_note_placeholder),
                                value = noteText,
                                onValueChange = { noteText = it },
                                minLines = 2,
                            )

                            Spacer(Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    val rupees = amountInt ?: return@Button
                                    if (rupees <= 0) return@Button
                                    onSave(rupees, titleText.trim(), noteText.trim(), selectedCategory)
                                },
                                enabled = canSave,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1E3A5F),
                                    disabledContainerColor = Color(0xFF9CA3AF),
                                    contentColor = Color.White,
                                    disabledContentColor = Color.White.copy(alpha = 0.7f),
                                ),
                                elevation = ButtonDefaults.buttonElevation(0.dp),
                            ) {
                                Text(
                                    text = stringResource(R.string.add_expense_save),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                )
                            }

                            Spacer(Modifier.height(16.dp))
                        }

                        if (showNumericKeypad) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(KeypadStripBackground),
                            ) {
                                Column(Modifier.navigationBarsPadding()) {
                                    NumericKeypad(
                                        onKey = { key ->
                                            amountRaw = appendAmountKey(amountRaw, key)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SoftLabeledField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    minLines: Int = 1,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FieldBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MutedText,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(6.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                color = Color(0xFF111827),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            ),
            minLines = minLines,
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = MutedText.copy(alpha = 0.65f),
                            fontSize = 16.sp,
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}

@Composable
private fun CategoryChip(
    category: ExpenseIconStyle,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val spec = categoryVisual(category)
    val borderColor = if (selected) spec.tint else Color.Transparent
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
        ),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .border(
                    width = if (selected) 3.dp else 0.dp,
                    color = borderColor,
                    shape = CircleShape,
                )
                .clip(CircleShape)
                .background(spec.softBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = spec.icon,
                contentDescription = null,
                tint = spec.tint,
                modifier = Modifier.size(26.dp),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = categoryChipLabel(category),
            fontSize = 11.sp,
            lineHeight = 13.sp,
            color = Color(0xFF374151),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}

private data class CategoryVisual(
    val icon: ImageVector,
    val tint: Color,
    val softBg: Color,
)

private fun categoryVisual(category: ExpenseIconStyle): CategoryVisual = when (category) {
    ExpenseIconStyle.Entertainment -> CategoryVisual(
        icon = Icons.Outlined.PlayCircle,
        tint = Color(0xFF7C4DFF),
        softBg = Color(0xFFE9DEFD),
    )
    ExpenseIconStyle.Food -> CategoryVisual(
        icon = Icons.Outlined.Restaurant,
        tint = Color(0xFFE67E22),
        softBg = Color(0xFFFFE6C9),
    )
    ExpenseIconStyle.Transfer -> CategoryVisual(
        icon = Icons.AutoMirrored.Outlined.ArrowForward,
        tint = Color(0xFF2E7D32),
        softBg = Color(0xFFD8F1DD),
    )
    ExpenseIconStyle.Bills -> CategoryVisual(
        icon = Icons.Outlined.CreditCard,
        tint = Color(0xFF2563EB),
        softBg = Color(0xFFE5EEFB),
    )
    ExpenseIconStyle.Travel -> CategoryVisual(
        icon = Icons.Outlined.LocationOn,
        tint = Color(0xFFE91E63),
        softBg = Color(0xFFFFE4EC),
    )
}

@Composable
private fun categoryChipLabel(category: ExpenseIconStyle): String = when (category) {
    ExpenseIconStyle.Entertainment -> stringResource(R.string.category_entertainment)
    ExpenseIconStyle.Food -> stringResource(R.string.category_food)
    ExpenseIconStyle.Transfer -> stringResource(R.string.category_transfer)
    ExpenseIconStyle.Bills -> stringResource(R.string.category_bills)
    ExpenseIconStyle.Travel -> stringResource(R.string.category_travel)
}

private fun appendAmountKey(raw: String, key: String): String {
    return when (key) {
        "back" -> raw.dropLast(1)
        "." -> when {
            raw.contains('.') -> raw
            raw.isEmpty() -> "0."
            else -> raw + "."
        }
        else -> {
            if (key == "0" && raw == "0") {
                raw
            } else if (raw == "0" && key != ".") {
                key
            } else {
                val afterDot = raw.substringAfter('.', missingDelimiterValue = "")
                if (raw.contains('.') && afterDot.length >= 2) {
                    raw
                } else {
                    raw + key
                }
            }
        }
    }
}

private fun parseAmountToRupeesInt(raw: String): Int? {
    val t = raw.trim()
    if (t.isEmpty() || t == "." || t.endsWith(".")) return null
    return t.toDoubleOrNull()?.let { kotlin.math.round(it).toInt() }
}

@Composable
private fun NumericKeypad(
    onKey: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val grid = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(".", "0", "back"),
    )
    val divider = Color(0xFFE5E7EB)
    Column(
        modifier = modifier.background(KeypadStripBackground),
    ) {
        grid.forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                row.forEachIndexed { colIndex, key ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onKey(key) },
                        contentAlignment = Alignment.Center,
                    ) {
                        when (key) {
                            "back" -> Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Backspace,
                                contentDescription = stringResource(R.string.add_expense_keypad_back_cd),
                                tint = Color(0xFF374151),
                                modifier = Modifier.size(24.dp),
                            )
                            else -> Text(
                                text = key,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF111827),
                            )
                        }
                    }
                    if (colIndex < row.lastIndex) {
                        VerticalDivider(
                            modifier = Modifier.fillMaxHeight(),
                            thickness = 1.dp,
                            color = divider,
                        )
                    }
                }
            }
            if (rowIndex < grid.lastIndex) {
                HorizontalDivider(thickness = 1.dp, color = divider)
            }
        }
    }
}
