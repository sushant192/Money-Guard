package com.example.moneyguard.features.dashboard.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
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
import kotlinx.coroutines.launch
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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    // Drawer is fixed at 60% of the screen width, per design — wide enough
    // for the avatar header + items, but never edge-to-edge.
    val drawerWidth = (LocalConfiguration.current.screenWidthDp * 0.6f).dp
    val openDrawer: () -> Unit = { scope.launch { drawerState.open() } }
    val closeDrawer: () -> Unit = { scope.launch { drawerState.close() } }

    // Material 3's ModalNavigationDrawer always opens from the *start* edge.
     // Flipping LayoutDirection to RTL just for the drawer puts the sheet on
     // the right (where the profile icon lives); we restore LTR on the actual
     // sheet + content so layouts inside read top-to-bottom, left-to-right.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        // Keep the expense sheet outside the drawer’s main content so the modal
        // covers the full window (including the bottom nav) instead of stacking
        // with it like a sibling column.
        Box(modifier = Modifier.fillMaxSize()) {
            ModalNavigationDrawer(
                modifier = Modifier.fillMaxSize(),
                drawerState = drawerState,
                // Only allow swipe-to-close once the drawer is already open. Disabling
                // the swipe-to-open gesture avoids stealing horizontal scrolls from
                // future tab content / charts.
                gesturesEnabled = drawerState.isOpen,
                drawerContent = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        // We deliberately use a bare Surface (not ModalDrawerSheet)
                        // here so we can set the width to exactly 60% of the screen.
                        // ModalDrawerSheet internally caps the sheet at
                        // DrawerDefaults.MaximumDrawerWidth (360.dp) which makes
                        // the drawer look narrow on larger phones / tablets.
                        Surface(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(drawerWidth),
                            // Round the inner edge of the right-side sheet so it
                            // reads as a "card" tucked against the screen's right.
                            shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
                            color = Color.White,
                            tonalElevation = 0.dp,
                            shadowElevation = 1.dp,
                        ) {
                            DashboardDrawerContent(
                                userName = state.userName,
                                userEmail = state.userEmail,
                                onManageLimits = {
                                    event.onTabSelected(DashboardTab.Limits)
                                    closeDrawer()
                                },
                                onEditProfile = closeDrawer,
                                onNotifications = closeDrawer,
                                onExportData = closeDrawer,
                                onLogout = {
                                    // Close first so the drawer animation isn't fighting
                                    // a graph-level navigation; the screen will be torn
                                    // down anyway, but this keeps things smooth on slower
                                    // devices.
                                    closeDrawer()
                                    event.onLogoutClick()
                                },
                            )
                        }
                    }
                },
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    DashboardScaffold(
                        state = state,
                        event = event,
                        onMenuClick = openDrawer,
                    )
                }
            }
            if (state.showAddExpenseSheet) {
                // Modal must be LTR: the drawer uses a parent RTL hack; without this,
                // rows, keypad order, and horizontalScroll all mirror incorrectly.
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    AddExpenseBottomSheet(
                        onDismiss = event::onDismissAddExpenseSheet,
                        onSave = { amount, title, note, category ->
                            event.onSaveManualExpense(amount, title, note, category)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardScaffold(
    state: HomeUiState,
    event: HomeUiEvents,
    onMenuClick: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // Home draws under the status bar with its own blue background, so we
        // keep the Scaffold root transparent and don't let it consume the top
        // inset (each tab handles its own top padding via statusBarsPadding).
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            if (state.selectedTab == DashboardTab.Home && !state.showAddExpenseSheet) {
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
            if (!state.showAddExpenseSheet) {
                // Wrap the NavigationBar in a Column so we can paint a thin
                // hairline divider above it — gives the tab bar a clear edge
                // against the white content area, matching the design.
                Column {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color(0xFFE5E7EB),
                    )
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
                DashboardTab.Home -> HomeTabContent(
                    state = state,
                    event = event,
                    onMenuClick = onMenuClick,
                )
                DashboardTab.History -> HistoryTabContent(state = state)
                DashboardTab.Stats -> StatsTabContent(state = state)
                DashboardTab.Limits -> LimitsTabContent(state = state, event = event)
            }
        }
    }
}

@Composable
private fun HomeTabContent(
    state: HomeUiState,
    event: HomeUiEvents,
    onMenuClick: () -> Unit,
) {
    val context = LocalContext.current
    val scroll = rememberScrollState()

    if (!state.hasNotificationAccess) {
        PermissionPendingHomeContent(
            state = state,
            onMenuClick = onMenuClick,
            onGrantClick = { event.onGrantNotificationAccessClick(context) },
        )
        return
    }

    if (state.todayExpenses.isEmpty()) {
        EmptyHomeContent(
            state = state,
            onMenuClick = onMenuClick,
        )
        return
    }

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
                    onMenuClick = onMenuClick,
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
private fun EmptyHomeContent(
    state: HomeUiState,
    onMenuClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
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
                    onMenuClick = onMenuClick,
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.home_today_expenses),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        modifier = Modifier.align(Alignment.Start),
                    )
                    Spacer(Modifier.height(36.dp))
                    NoExpensesIllustration()
                    Spacer(Modifier.height(28.dp))
                    Text(
                        text = stringResource(R.string.home_empty_title),
                        color = Color(0xFF111827),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.home_empty_body),
                        color = MutedText,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(24.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFEFF5FF),
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = BrandBlue,
                                modifier = Modifier.size(22.dp),
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.home_empty_tip),
                                color = BrandBlue,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionPendingHomeContent(
    state: HomeUiState,
    onMenuClick: () -> Unit,
    onGrantClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
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
                    onMenuClick = onMenuClick,
                )
                Spacer(Modifier.height(20.dp))
                AwaitingAccessCard(dailyLimitRupees = state.dailyLimitRupees)
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.home_today_expenses),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        modifier = Modifier.align(Alignment.Start),
                    )
                    Spacer(Modifier.height(36.dp))
                    NotificationAccessIllustration()
                    Spacer(Modifier.height(28.dp))
                    Text(
                        text = stringResource(R.string.home_permission_needed_title),
                        color = Color(0xFF111827),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.home_permission_needed_body),
                        color = MutedText,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(26.dp))
                    Button(
                        onClick = onGrantClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandBlue,
                            contentColor = Color.White,
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.home_permission_needed_cta),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                        )
                    }
                    Spacer(Modifier.height(18.dp))
                    Text(
                        text = stringResource(R.string.home_permission_needed_note),
                        color = MutedText.copy(alpha = 0.75f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun AwaitingAccessCard(
    dailyLimitRupees: Int,
) {
    val nf = rememberInrFormatter()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.14f),
                shape = RoundedCornerShape(20.dp),
            )
            .padding(horizontal = 18.dp, vertical = 18.dp),
    ) {
        val faded = Color.White.copy(alpha = 0.52f)
        Text(
            text = stringResource(R.string.home_spent_today_label),
            color = faded,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.home_awaiting_access),
            color = Color.White.copy(alpha = 0.70f),
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.4).sp,
        )
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(
                    R.string.home_daily_limit_row,
                    nf.formatInrPlain(dailyLimitRupees),
                ),
                color = faded,
                fontSize = 13.sp,
            )
            Text(
                text = "—",
                color = faded,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = Color.White.copy(alpha = 0.30f),
            trackColor = Color.White.copy(alpha = 0.14f),
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
                value = "—",
                modifier = Modifier.weight(1f),
            )
            StatPill(
                label = stringResource(R.string.home_transactions_label),
                value = "—",
                modifier = Modifier.weight(1f),
            )
            StatPill(
                label = stringResource(R.string.home_saved_label),
                value = "—",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun NotificationAccessIllustration() {
    Box(
        modifier = Modifier.size(width = 130.dp, height = 92.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(96.dp)
                .height(72.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFFFFBF2))
                .border(
                    width = 1.5.dp,
                    color = Color(0xFFF5DCA6),
                    shape = RoundedCornerShape(18.dp),
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (it == 1) 0.72f else 0.58f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFE8C7)),
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(x = (-12).dp, y = (-6).dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFF6D9))
                .border(1.5.dp, Color(0xFFF6D36B), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = Color(0xFFE0A900),
                modifier = Modifier.size(22.dp),
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-8).dp, y = (2).dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFFFDB022)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.PriorityHigh,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun NoExpensesIllustration() {
    Box(
        modifier = Modifier.size(width = 130.dp, height = 92.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(92.dp)
                .height(72.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFF8FAFF))
                .border(
                    width = 1.5.dp,
                    color = Color(0xFFD6E1FF),
                    shape = RoundedCornerShape(18.dp),
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (it == 1) 0.70f else 0.56f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE2E9FB)),
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 10.dp, y = 4.dp)
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color(0xFFD6E1FF), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = Color(0xFF77A7FF),
                modifier = Modifier.size(14.dp),
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-2).dp, y = (-6).dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(BrandBlue),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun HistoryTabContent(state: HomeUiState) {
    val scroll = rememberScrollState()
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
            // Blue header — same shell as Home, just different copy.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HomeHeaderBlue)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(top = 8.dp, bottom = 32.dp),
            ) {
                Text(
                    text = stringResource(R.string.history_title),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.history_subtitle),
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
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
                    state.historyGroups.forEachIndexed { index, group ->
                        if (index > 0) Spacer(Modifier.height(20.dp))
                        Text(
                            text = group.title.uppercase(),
                            color = MutedText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.4.sp,
                        )
                        Spacer(Modifier.height(12.dp))
                        group.items.forEach { item ->
                            HistoryRow(item = item)
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(item: HistoryItemUi) {
    val nf = rememberInrFormatter()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FieldBackground)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ExpenseIconBadge(
            style = item.iconStyle,
            useUpiPaymentIcon = item.title.startsWith("UPI to ", ignoreCase = true),
        )
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
                text = item.time,
                fontSize = 13.sp,
                color = MutedText,
            )
        }
        Text(
            text = "−${nf.formatInrPlain(item.amountRupees)}",
            color = ErrorMain,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
        )
    }
}

// region — Stats tab

@Composable
private fun StatsTabContent(state: HomeUiState) {
    val scroll = rememberScrollState()
    val nf = rememberInrFormatter()
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
            // Compact blue header — three stacked lines, no large card.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HomeHeaderBlue)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(top = 8.dp, bottom = 32.dp),
            ) {
                Text(
                    text = stringResource(R.string.stats_this_month),
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.stats_amount_spent,
                        nf.formatInrPlain(state.monthSpentRupees),
                    ),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.4).sp,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.stats_out_of_budget,
                        nf.formatInrPlain(state.monthlyBudgetRupees),
                    ),
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 14.sp,
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
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
                    SpendingByCategorySection(
                        categories = state.categoryBreakdown,
                        formatter = nf,
                    )

                    Spacer(Modifier.height(28.dp))

                    DailyThisWeekSection(bars = state.weekDailyBars)
                }
            }
        }
    }
}

@Composable
private fun SpendingByCategorySection(
    categories: List<CategorySpendUi>,
    formatter: InrFormatter,
) {
    Text(
        text = stringResource(R.string.stats_spending_by_category),
        color = Color(0xFF111827),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(16.dp))
    val maxAmount = categories.maxOfOrNull { it.amountRupees }?.coerceAtLeast(1) ?: 1
    categories.forEachIndexed { index, item ->
        if (index > 0) Spacer(Modifier.height(16.dp))
        CategoryProgressRow(
            name = stringResource(item.nameRes),
            amountText = formatter.formatInrPlain(item.amountRupees),
            fraction = item.amountRupees.toFloat() / maxAmount,
            color = item.color,
        )
    }
}

@Composable
private fun CategoryProgressRow(
    name: String,
    amountText: String,
    fraction: Float,
    color: Color,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = name,
                color = Color(0xFF111827),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = amountText,
                color = Color(0xFF111827),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { fraction.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = FieldBackground,
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
    }
}

@Composable
private fun DailyThisWeekSection(bars: List<DayBarUi>) {
    Text(
        text = stringResource(R.string.stats_daily_this_week),
        color = Color(0xFF111827),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(18.dp))
    WeekBarChart(bars = bars)
}

@Composable
private fun WeekBarChart(bars: List<DayBarUi>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            bars.forEach { bar ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    val barColor = when (bar.kind) {
                        BarKind.Today -> HomeHeaderBlue
                        BarKind.Past -> HomeHeaderBlue.copy(alpha = 0.45f)
                        BarKind.Future -> Color(0xFFE5E7EB)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.55f)
                            // Smallest rendered bar is ~5% so even zero-spend
                            // days show a visible stub like the prototype.
                            .fillMaxHeight(bar.heightFraction.coerceIn(0.05f, 1f))
                            .clip(RoundedCornerShape(8.dp))
                            .background(barColor),
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            bars.forEach { bar ->
                Text(
                    text = bar.label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = if (bar.kind == BarKind.Future) {
                        MutedText.copy(alpha = 0.55f)
                    } else {
                        MutedText
                    },
                    fontWeight = if (bar.kind == BarKind.Today) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}

// endregion

// region — Limits tab

@Composable
private fun LimitsTabContent(
    state: HomeUiState,
    event: HomeUiEvents,
) {
    val scroll = rememberScrollState()
    val nf = rememberInrFormatter()
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
                Text(
                    text = stringResource(R.string.limits_title),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.limits_subtitle),
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
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
                    AlertBanner(
                        percentUsed = state.budgetUsedPercent,
                        remainingText = nf.formatInrPlain(state.remainingRupees),
                    )

                    Spacer(Modifier.height(16.dp))

                    LimitCard(
                        title = stringResource(R.string.limits_daily),
                        totalText = nf.formatInrPlain(state.dailyLimitRupees),
                        spentText = stringResource(R.string.limits_spent, nf.formatInrPlain(state.spentTodayRupees)),
                        leftText = stringResource(R.string.limits_left, nf.formatInrPlain(state.remainingRupees)),
                        fraction = state.spentTodayRupees.toFloat() / state.dailyLimitRupees.coerceAtLeast(1),
                    )

                    Spacer(Modifier.height(12.dp))

                    val weeklyLeft = (state.weeklyLimitRupees - state.weeklySpentRupees).coerceAtLeast(0)
                    LimitCard(
                        title = stringResource(R.string.limits_weekly),
                        totalText = nf.formatInrPlain(state.weeklyLimitRupees),
                        spentText = stringResource(R.string.limits_spent, nf.formatInrPlain(state.weeklySpentRupees)),
                        leftText = stringResource(R.string.limits_left, nf.formatInrPlain(weeklyLeft)),
                        fraction = state.weeklySpentRupees.toFloat() / state.weeklyLimitRupees.coerceAtLeast(1),
                    )

                    Spacer(Modifier.height(12.dp))

                    val monthlyLeft = (state.monthlyBudgetRupees - state.monthSpentRupees).coerceAtLeast(0)
                    LimitCard(
                        title = stringResource(R.string.limits_monthly),
                        totalText = nf.formatInrPlain(state.monthlyBudgetRupees),
                        spentText = stringResource(R.string.limits_spent, nf.formatInrPlain(state.monthSpentRupees)),
                        leftText = stringResource(R.string.limits_left, nf.formatInrPlain(monthlyLeft)),
                        fraction = state.monthSpentRupees.toFloat() / state.monthlyBudgetRupees.coerceAtLeast(1),
                    )

                    Spacer(Modifier.height(16.dp))

                    AlertThresholdCard(
                        selected = state.alertThreshold,
                        onSelect = event::onAlertThresholdSelect,
                    )
                }
            }
        }
    }
}

@Composable
private fun AlertBanner(percentUsed: Int, remainingText: String) {
    // Soft peach card with star + warning text. Uses the same design tokens
    // as other "informational" surfaces — hand-tuned hex values rather than
    // Material defaults so the banner reads as a brand alert, not an error.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFE9D5))
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Star,
            contentDescription = null,
            tint = Color(0xFFF59E0B),
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.limits_alert_used, percentUsed),
                color = Color(0xFFB45309),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.limits_remaining_today, remainingText),
                color = Color(0xFF8A5F1A),
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun LimitCard(
    title: String,
    totalText: String,
    spentText: String,
    leftText: String,
    fraction: Float,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FieldBackground)
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                color = Color(0xFF111827),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = totalText,
                color = BrandBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { fraction.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = BrandBlue,
            trackColor = Color.White,
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = spentText,
                color = MutedText,
                fontSize = 13.sp,
            )
            Text(
                text = leftText,
                color = MutedText,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun AlertThresholdCard(
    selected: AlertThreshold,
    onSelect: (AlertThreshold) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FieldBackground)
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Text(
            text = stringResource(R.string.limits_alert_threshold),
            color = Color(0xFF111827),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AlertThreshold.entries.forEach { threshold ->
                ThresholdChip(
                    label = stringResource(threshold.labelRes),
                    isSelected = threshold == selected,
                    onClick = { onSelect(threshold) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ThresholdChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) BrandBlue else Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else BrandBlue,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
        )
    }
}

// endregion

// region — Side drawer

@Composable
private fun DashboardDrawerContent(
    userName: String,
    userEmail: String,
    onManageLimits: () -> Unit,
    onEditProfile: () -> Unit,
    onNotifications: () -> Unit,
    onExportData: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Blue header — avatar + name + email, sits behind the status bar.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(HomeHeaderBlue)
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = initialsOf(userName),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = userName,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = userEmail,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
            )
        }

        // White sheet with rounded top edge — items go inside this.
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-20).dp),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color.White,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
            ) {
                DrawerItem(
                    icon = Icons.Outlined.CreditCard,
                    iconBackground = Color(0xFFE5EEFB),
                    iconTint = BrandBlue,
                    label = stringResource(R.string.drawer_manage_limits),
                    onClick = onManageLimits,
                )
                DrawerDivider()
                DrawerItem(
                    icon = Icons.Outlined.Person,
                    iconBackground = Color(0xFFE5EEFB),
                    iconTint = BrandBlue,
                    label = stringResource(R.string.drawer_edit_profile),
                    onClick = onEditProfile,
                )
                DrawerDivider()
                DrawerItem(
                    icon = Icons.Outlined.WbSunny,
                    iconBackground = Color(0xFFFFE6CC),
                    iconTint = Color(0xFFF59E0B),
                    label = stringResource(R.string.drawer_notifications),
                    onClick = onNotifications,
                )
                DrawerDivider()
                DrawerItem(
                    icon = Icons.Outlined.AccountBalanceWallet,
                    iconBackground = Color(0xFFD8F1DD),
                    iconTint = Color(0xFF2E7D32),
                    label = stringResource(R.string.drawer_export_data),
                    onClick = onExportData,
                )
                DrawerDivider()
                DrawerItem(
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    iconBackground = Color(0xFFFEE2E2),
                    iconTint = ErrorMain,
                    label = stringResource(R.string.drawer_logout),
                    labelColor = ErrorMain,
                    onClick = onLogout,
                )
            }
        }
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    label: String,
    onClick: () -> Unit,
    labelColor: Color = Color(0xFF111827),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBackground),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = label,
            color = labelColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = MutedText,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun DrawerDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 1.dp,
        color = Color(0xFFEEF1F4),
    )
}

/** "Rahul Sharma" -> "RS", "Rahul" -> "R", "" -> "". */
private fun initialsOf(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        parts.isEmpty() -> ""
        parts.size == 1 -> parts.first().take(1).uppercase()
        else -> (parts.first().take(1) + parts.last().take(1)).uppercase()
    }
}

// endregion

@Composable
private fun HomeHeaderRow(
    greetingPrefix: String,
    userName: String,
    onMenuClick: () -> Unit,
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
            onClick = onMenuClick,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = stringResource(R.string.drawer_open_content_description),
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
        ExpenseIconBadge(
            style = item.iconStyle,
            useUpiPaymentIcon = item.paymentLabel.equals("UPI", ignoreCase = true),
        )
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
                override fun onSeeAllExpensesClick() = Unit
                override fun onFabClick() = Unit
                override fun onDismissAddExpenseSheet() = Unit
                override fun onSaveManualExpense(
                    amountRupees: Int,
                    title: String,
                    note: String,
                    category: ExpenseIconStyle,
                ) = Unit

                override fun onAlertThresholdSelect(threshold: AlertThreshold) = Unit
                override fun onGrantNotificationAccessClick(activityContext: android.content.Context) = Unit
                override fun onLogoutClick() = Unit
            },
        )
    }
}
