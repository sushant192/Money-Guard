package com.example.moneyguard.features.dashboard.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowCircleRight
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneyguard.R
import com.example.moneyguard.core.arch.BaseScreen
import com.example.moneyguard.ui.theme.BrandBlue
import com.example.moneyguard.ui.theme.ErrorMain
import com.example.moneyguard.ui.theme.FieldBackground
import com.example.moneyguard.ui.theme.HomeHeaderBlue
import com.example.moneyguard.ui.theme.MoneyGuardTheme
import com.example.moneyguard.ui.theme.MutedText
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    BaseScreen(viewModel) { state ->
        HomeUiComponents(
            state = state.value,
            event = viewModel,
        )
    }
}

@Composable
private fun HomeUiComponents(
    state: HomeUiState,
    event: HomeUiEvents,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // Home draws under the status bar with its own blue background, so we
        // keep the Scaffold root transparent and don't let it consume the top
        // inset (each tab handles its own top padding via statusBarsPadding).
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            if (state.selectedTab == DashboardTab.Home) {
                FloatingActionButton(
                    onClick = event::onFabClick,
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    containerColor = BrandBlue,
                    contentColor = Color.White,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = stringResource(R.string.home_fab_add_expense),
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp,
            ) {
                DashboardTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = state.selectedTab == tab,
                        onClick = { event.onTabSelected(tab) },
                        icon = {
                            Icon(
                                imageVector = tab.icon(),
                                contentDescription = stringResource(tab.labelRes),
                            )
                        },
                        label = { Text(text = stringResource(tab.labelRes)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandBlue,
                            selectedTextColor = BrandBlue,
                            indicatorColor = BrandBlue.copy(alpha = 0.12f),
                            unselectedIconColor = MutedText,
                            unselectedTextColor = MutedText,
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (state.selectedTab) {
                DashboardTab.Home -> HomeTabContent(state = state, event = event)
                else -> PlaceholderTab(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun PlaceholderTab(modifier: Modifier = Modifier) {
    // We disabled Scaffold's top inset, so each tab is responsible for keeping
    // its own content out from under the status bar.
    Box(
        modifier = modifier
            .background(Color.White)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.tab_placeholder),
            style = MaterialTheme.typography.titleMedium,
            color = MutedText,
        )
    }
}

@Composable
private fun HomeTabContent(
    state: HomeUiState,
    event: HomeUiEvents,
) {
    val scroll = rememberScrollState()
    // Outer Box with a white background so any "empty" area below the
    // expenses (when content is shorter than the screen, or when scrolled
    // up) reads as white instead of the blue header colour bleeding through.
    // The blue header is now drawn ONLY on the inner header column.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HomeHeaderBlue)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(top = 8.dp, bottom = 32.dp),
            ) {
                HomeHeaderRow(
                    greetingPrefix = state.greetingPrefix,
                    userName = state.userName,
                    onProfileClick = event::onProfileClick,
                )
                Spacer(Modifier.height(20.dp))
                SpentTodayCard(
                    spentRupees = state.spentTodayRupees,
                    dailyLimitRupees = state.dailyLimitRupees,
                    budgetPercent = state.budgetUsedPercent,
                    remainingRupees = state.remainingRupees,
                    transactionCount = state.transactionCount,
                    savedRupees = state.savedRupees,
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    // Pull the white sheet up so it overlaps the blue header
                    // for the rounded-shoulder look.
                    .offset(y = (-20).dp),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = Color.White,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 24.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.home_today_expenses),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827),
                        )
                        Text(
                            text = stringResource(R.string.home_see_all),
                            color = BrandBlue,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { event.onSeeAllExpensesClick() }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                    Spacer(Modifier.height(18.dp))
                    state.todayExpenses.forEach { expense ->
                        ExpenseRow(item = expense)
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeaderRow(
    greetingPrefix: String,
    userName: String,
    onProfileClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = greetingPrefix,
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 15.sp,
            )
            Text(
                text = userName,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        IconButton(
            onClick = onProfileClick,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = stringResource(R.string.home_profile_content_description),
                tint = Color.White,
                modifier = Modifier.size(26.dp),
            )
        }
    }
}

@Composable
private fun SpentTodayCard(
    spentRupees: Int,
    dailyLimitRupees: Int,
    budgetPercent: Int,
    remainingRupees: Int,
    transactionCount: Int,
    savedRupees: Int,
) {
    val nf = rememberInrFormatter()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(horizontal = 18.dp, vertical = 18.dp),
    ) {
        Text(
            text = stringResource(R.string.home_spent_today_label),
            color = Color.White.copy(alpha = 0.78f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = nf.formatInr(spentRupees),
            color = Color.White,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
        )
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.home_daily_limit_row, nf.formatInrPlain(dailyLimitRupees)),
                color = Color.White.copy(alpha = 0.88f),
                fontSize = 13.sp,
            )
            Text(
                text = "$budgetPercent%",
                color = Color.White.copy(alpha = 0.88f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { budgetPercent.coerceIn(0, 100) / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.22f),
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatPill(
                label = stringResource(R.string.home_remaining_label),
                value = nf.formatInrPlain(remainingRupees),
                modifier = Modifier.weight(1f),
            )
            StatPill(
                label = stringResource(R.string.home_transactions_label),
                value = transactionCount.toString(),
                modifier = Modifier.weight(1f),
            )
            StatPill(
                label = stringResource(R.string.home_saved_label),
                value = nf.formatInrPlain(savedRupees),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.78f),
            fontSize = 11.sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ExpenseRow(item: ExpenseItemUi) {
    val nf = rememberInrFormatter()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FieldBackground)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ExpenseIconBadge(style = item.iconStyle)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF111827),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.metaLine,
                fontSize = 13.sp,
                color = MutedText,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "−${nf.formatInrPlain(item.amountRupees)}",
                color = ErrorMain,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.paymentLabel,
                fontSize = 12.sp,
                color = MutedText,
            )
        }
    }
}

@Composable
private fun ExpenseIconBadge(style: ExpenseIconStyle) {
    // Pastel tile background + saturated glyph on top, matching the prototype.
    val bg: Color
    val tint: Color
    val icon: ImageVector
    when (style) {
        ExpenseIconStyle.Entertainment -> {
            bg = Color(0xFFE9DEFD) // soft lavender
            tint = Color(0xFF7C4DFF)
            icon = Icons.Outlined.PlayCircle
        }
        ExpenseIconStyle.Transfer -> {
            bg = Color(0xFFD8F1DD) // soft mint
            tint = Color(0xFF2E7D32)
            icon = Icons.Outlined.ArrowCircleRight
        }
        ExpenseIconStyle.Food -> {
            bg = Color(0xFFFFE6C9) // soft peach
            tint = Color(0xFFE67E22)
            icon = Icons.Outlined.Restaurant
        }
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

private fun DashboardTab.icon(): ImageVector = when (this) {
    DashboardTab.Home -> Icons.Outlined.BarChart
    DashboardTab.History -> Icons.Outlined.Schedule
    DashboardTab.Stats -> Icons.AutoMirrored.Outlined.ShowChart
    DashboardTab.Limits -> Icons.Outlined.CreditCard
}

@Composable
private fun rememberInrFormatter(): InrFormatter = remember { InrFormatter() }

private class InrFormatter {
    private val inr = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    fun formatInr(amount: Int): String = inr.format(amount).trim()

    fun formatInrPlain(amount: Int): String {
        val nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN"))
        return "₹${nf.format(amount)}"
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomePreview() {
    MoneyGuardTheme {
        HomeUiComponents(
            state = HomeUiState.Initial,
            event = object : HomeUiEvents {
                override fun onTabSelected(tab: DashboardTab) = Unit
                override fun onProfileClick() = Unit
                override fun onSeeAllExpensesClick() = Unit
                override fun onFabClick() = Unit
            },
        )
    }
}
