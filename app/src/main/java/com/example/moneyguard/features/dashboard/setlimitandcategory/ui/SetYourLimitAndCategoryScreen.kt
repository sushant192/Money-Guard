package com.example.moneyguard.features.dashboard.setlimitandcategory.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.outlined.ArrowCircleRight
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.SmartDisplay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneyguard.R
import com.example.moneyguard.core.arch.BaseScreen
import com.example.moneyguard.ui.theme.BrandGradient
import com.example.moneyguard.ui.theme.MoneyGuardTheme

@Composable
fun SetYourLimitAndCategoryScreen(viewModel: SetYourLimitAndCategoryViewModel) {
    BaseScreen(viewModel) { state ->
        SetYourLimitAndCategoryUiComponents(
            state = state.value,
            event = viewModel
        )
    }
}

@Composable
private fun SetYourLimitAndCategoryUiComponents(
    state: SetYourLimitAndCategoryUiState,
    event: SetYourLimitAndCategoryUiEvents
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = BrandGradient)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Spacer(Modifier.height(16.dp))

        StepDots(
            activeIndex = state.step.dotIndex,
            total = STEP_DOT_COUNT,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        // Scroll state hoisted so it survives step swaps.
        val categoriesScroll = rememberScrollState()

        // No AnimatedContent / Crossfade here: layering fade-out + fade-in made
        // the stagger obvious (“limit disappears, then categories appear”) and
        // felt slower than an instant swap.
        when (state.step) {
            SetLimitStep.LIMIT -> DailyLimitSection(
                limit = state.limit,
                onLimitChange = event::onLimitChange,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 24.dp)
            )
            SetLimitStep.CATEGORIES -> Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .verticalScroll(categoriesScroll)
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 24.dp)
            ) {
                CategorySection(
                    selected = state.selectedCategories,
                    onToggle = event::onCategoryToggle
                )
            }
            SetLimitStep.NOTIFICATION_ACCESS -> NotificationAccessSection(
                state = state,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 24.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp, top = 8.dp)
        ) {
            when (state.step) {
                SetLimitStep.LIMIT -> {
                    PrimaryButton(
                        label = stringResource(R.string.set_limit_continue),
                        enabled = !state.isLoading,
                        onClick = event::onContinueClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                SetLimitStep.CATEGORIES -> {
                    PrimaryButton(
                        label = stringResource(R.string.set_limit_continue),
                        enabled = !state.isLoading,
                        onClick = event::onContinueClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    SecondaryButton(
                        label = stringResource(R.string.set_limit_back),
                        enabled = !state.isLoading,
                        onClick = event::onBackClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                SetLimitStep.NOTIFICATION_ACCESS -> {
                    when {
                        state.hasNotificationAccess -> {
                            PrimaryButton(
                                label = stringResource(R.string.set_limit_cta),
                                enabled = !state.isLoading,
                                onClick = event::onFinishClick,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        state.hasOpenedNotificationSettings -> {
                            PrimaryButton(
                                label = stringResource(R.string.set_limit_notification_open_settings),
                                enabled = !state.isLoading,
                                onClick = { event.onOpenNotificationSettingsClick(context) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            SecondaryButton(
                                label = stringResource(R.string.set_limit_notification_later),
                                enabled = !state.isLoading,
                                onClick = event::onFinishClick,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        else -> {
                            PrimaryButton(
                                label = stringResource(R.string.set_limit_notification_open_settings),
                                enabled = !state.isLoading,
                                onClick = { event.onOpenNotificationSettingsClick(context) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

// region — Limit section

@Composable
private fun DailyLimitSection(
    limit: Int,
    onLimitChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Layout intent: the whole block — icon, title, subtitle, card, slider,
    // and min/max row — sits as one vertically-centered cluster inside the
    // available area (between the step dots and the bottom buttons). We use
    // Arrangement.Center on the Column so all children flow together with
    // their natural spacers and the surrounding empty space distributes
    // equally above and below the cluster.
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        TranslucentIconBadge(
            icon = Icons.Outlined.Schedule,
            modifier = Modifier.size(76.dp),
            iconSize = 32.dp,
            cornerRadius = 18.dp
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.set_limit_title),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.set_limit_subtitle),
            color = Color.White.copy(alpha = 0.60f),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(32.dp))

        DailyLimitCard(limit = limit)

        Spacer(Modifier.height(20.dp))

        Slider(
            value = limit.toFloat(),
            onValueChange = { onLimitChange(it.toInt()) },
            valueRange = SetYourLimitAndCategoryUiState.MIN_LIMIT.toFloat()..
                SetYourLimitAndCategoryUiState.MAX_LIMIT.toFloat(),
            // We deliberately don't pass `steps` here. With ~94 intermediate
            // steps the Slider would draw 94 invisible tick marks every frame,
            // which made the Limit → Categories Crossfade feel laggy. The
            // ViewModel still snaps every emitted value to the nearest 100
            // (see snapToHundred), so the thumb visibly settles on multiples
            // of 100 without the per-frame tick overhead.
            colors = SliderDefaults.colors(
                thumbColor = Color.Black,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.30f),
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.set_limit_min),
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp
            )
            Text(
                text = stringResource(R.string.set_limit_max),
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun DailyLimitCard(limit: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.14f))
            .border(
                width = 1.5.dp,
                color = Color.White.copy(alpha = 0.20f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.set_limit_card_label),
            color = Color.White.copy(alpha = 0.60f),
            fontSize = 14.sp,
            fontWeight    = FontWeight.Medium,
            letterSpacing = 0.8.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "₹${"%,d".format(limit)}",
            color = Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp
        )
    }
}

// endregion

// region — Categories section

@Composable
private fun CategorySection(
    selected: Set<Category>,
    onToggle: (Category) -> Unit
) {
    // Cache the row partitioning across recompositions — the contents never
    // change, so there's no point in recomputing chunked(2) every time the
    // selection set changes.
    val categoryRows = remember { Category.entries.toList().chunked(2) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = stringResource(R.string.categories_title),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.categories_subtitle),
            color = Color.White.copy(alpha = 0.80f),
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(24.dp))

        // Render the categories in fixed pairs so we can stay inside a parent
        // verticalScroll without LazyVerticalGrid measurement issues.
        categoryRows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEachIndexed { index, category ->
                    if (index > 0) Spacer(Modifier.width(12.dp))
                    CategoryChip(
                        category = category,
                        isSelected = category in selected,
                        onClick = { onToggle(category) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) {
                    Spacer(Modifier.width(12.dp))
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun CategoryChip(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerAlpha = if (isSelected) 0.30f else 0.15f
    val borderAlpha = if (isSelected) 0.60f else 0.20f
    val borderWidth = if (isSelected) 1.5.dp else 1.dp

    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = containerAlpha),
            contentColor = Color.White
        ),
        border = BorderStroke(
            width = borderWidth,
            color = Color.White.copy(alpha = borderAlpha)
        ),
        contentPadding = PaddingValues(
            horizontal = 14.dp,
            vertical = 0.dp
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = category.icon(),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(category.displayName),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun Category.icon(): ImageVector = when (this) {
    Category.FOOD -> Icons.Outlined.Restaurant
    Category.ENTERTAINMENT -> Icons.Outlined.SmartDisplay
    Category.TRANSFER -> Icons.Outlined.ArrowCircleRight
    Category.TRAVEL -> Icons.Outlined.LocationOn
    Category.BILLS -> Icons.Outlined.CreditCard
    Category.SHOPPING -> Icons.Outlined.ShoppingBag
}

// endregion

// region — Notification access section

@Composable
private fun NotificationAccessSection(
    state: SetYourLimitAndCategoryUiState,
    modifier: Modifier = Modifier,
) {
    val titleRes = when {
        state.hasNotificationAccess -> R.string.set_limit_notification_granted_title
        state.hasOpenedNotificationSettings -> R.string.set_limit_notification_retry_title
        else -> R.string.set_limit_notification_title
    }
    val subtitleRes = when {
        state.hasNotificationAccess -> R.string.set_limit_notification_granted_subtitle
        state.hasOpenedNotificationSettings -> R.string.set_limit_notification_retry_subtitle
        else -> R.string.set_limit_notification_subtitle
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth(),
    ) {
        TranslucentIconBadge(
            icon = Icons.Outlined.NotificationsActive,
            modifier = Modifier.size(76.dp),
            iconSize = 32.dp,
            cornerRadius = 18.dp,
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(titleRes),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = stringResource(subtitleRes),
            color = Color.White.copy(alpha = 0.60f),
            fontSize = 15.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
        )
    }
}

// endregion

// region — Shared bits (step dots, badge, CTA)

private const val STEP_DOT_COUNT = 3

private val SetLimitStep.dotIndex: Int
    get() = when (this) {
        SetLimitStep.LIMIT -> 0
        SetLimitStep.CATEGORIES -> 1
        SetLimitStep.NOTIFICATION_ACCESS -> 2
    }

@Composable
private fun StepDots(
    activeIndex: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { index ->
            val isActive = index == activeIndex
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(6.dp)
                    .width(if (isActive) 22.dp else 6.dp)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(alpha = if (isActive) 0.95f else 0.35f)
                    )
            )
        }
    }
}

@Composable
private fun TranslucentIconBadge(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconSize: Dp = 28.dp,
    cornerRadius: Dp = 20.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.22f),
                shape = RoundedCornerShape(cornerRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
private fun PrimaryButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.20f),
            contentColor = Color.White,
            disabledContainerColor = Color.White.copy(alpha = 0.10f),
            disabledContentColor = Color.White.copy(alpha = 0.50f)
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = Color.White.copy(alpha = 0.30f)
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SecondaryButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White.copy(alpha = 0.40f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.35f)
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// endregion

@Preview(showBackground = true, showSystemUi = true, name = "Step 1 — Limit")
@Composable
private fun SetLimitStepPreview() {
    MoneyGuardTheme {
        SetYourLimitAndCategoryUiComponents(
            state = SetYourLimitAndCategoryUiState(
                step = SetLimitStep.LIMIT,
                limit = 2_500,
                selectedCategories = emptySet(),
                hasNotificationAccess = false,
                hasOpenedNotificationSettings = false,
                isLoading = false
            ),
            event = NoOpSetLimitEvents
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Step 2 — Categories")
@Composable
private fun SetCategoriesStepPreview() {
    MoneyGuardTheme {
        SetYourLimitAndCategoryUiComponents(
            state = SetYourLimitAndCategoryUiState(
                step = SetLimitStep.CATEGORIES,
                limit = 2_500,
                selectedCategories = setOf(Category.TRAVEL, Category.FOOD),
                hasNotificationAccess = false,
                hasOpenedNotificationSettings = false,
                isLoading = false
            ),
            event = NoOpSetLimitEvents
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Step 3 — Notification Access")
@Composable
private fun NotificationAccessStepPreview() {
    MoneyGuardTheme {
        SetYourLimitAndCategoryUiComponents(
            state = SetYourLimitAndCategoryUiState(
                step = SetLimitStep.NOTIFICATION_ACCESS,
                limit = 2_500,
                selectedCategories = setOf(Category.TRAVEL, Category.FOOD),
                hasNotificationAccess = false,
                hasOpenedNotificationSettings = true,
                isLoading = false
            ),
            event = NoOpSetLimitEvents
        )
    }
}

private val NoOpSetLimitEvents = object : SetYourLimitAndCategoryUiEvents {
    override fun onLimitChange(value: Int) = Unit
    override fun onCategoryToggle(category: Category) = Unit
    override fun onContinueClick() = Unit
    override fun onBackClick() = Unit
    override fun onOpenNotificationSettingsClick(activityContext: android.content.Context) = Unit
    override fun onFinishClick() = Unit
}
