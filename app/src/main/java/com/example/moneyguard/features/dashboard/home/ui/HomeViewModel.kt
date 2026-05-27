package com.example.moneyguard.features.dashboard.home.ui

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.moneyguard.core.arch.BaseComposeViewModel
import com.example.moneyguard.core.navigation.Destination
import com.example.moneyguard.core.navigation.Navigator
import com.example.moneyguard.core.notifications.NotificationAccessManager
import com.example.moneyguard.features.auth.domain.repository.AuthRepository
import com.example.moneyguard.features.auth.domain.usecase.LogoutUseCase
import com.example.moneyguard.features.dashboard.home.data.HomeExpenseSnapshot
import com.example.moneyguard.features.dashboard.home.data.toExpenseDetailUi
import com.example.moneyguard.features.dashboard.home.domain.repository.ExpenseRepository
import com.example.moneyguard.features.dashboard.setlimitandcategory.domain.usecase.ClearBudgetSetupUseCase
import com.example.moneyguard.features.dashboard.setlimitandcategory.domain.usecase.GetDailyLimitUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Named
import java.util.Calendar

@KoinViewModel
class HomeViewModel(
    @Named("AppNavigator") private val navigator: Navigator,
    private val notificationAccessManager: NotificationAccessManager,
    private val authRepository: AuthRepository,
    private val logoutUseCase: LogoutUseCase,
    private val clearBudgetSetup: ClearBudgetSetupUseCase,
    private val getDailyLimit: GetDailyLimitUseCase,
    private val expenseRepository: ExpenseRepository,
) : BaseComposeViewModel<HomeUiState>(),
    HomeUiEvents {

    private var cachedExpenses: List<com.example.moneyguard.data.local.entity.ExpenseEntity> =
        emptyList()

    private val _uiState = MutableStateFlow(
        HomeUiState.Initial.copy(
            greetingPrefix = greetingPrefixForHour(Calendar.getInstance()),
            userName = currentUserDisplayName(),
            userEmail = authRepository.currentUser?.email.orEmpty(),
            hasNotificationAccess = notificationAccessManager.hasNotificationAccess(),
        )
    )
    override val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refreshSavedDailyLimit()
        viewModelScope.launch {
            expenseRepository.observeAllExpenses().collect { entities ->
                cachedExpenses = entities
                val snapshot = HomeExpenseSnapshot.from(entities, Calendar.getInstance())
                _uiState.update { state ->
                    val limit = state.dailyLimitRupees.coerceAtLeast(1)
                    val remaining = (state.dailyLimitRupees - snapshot.spentTodayRupees).coerceAtLeast(0)
                    val usedPercent =
                        ((snapshot.spentTodayRupees * 100f) / limit).toInt().coerceAtLeast(0)
                    val refreshedDetail =
                        state.selectedExpenseDetail?.let { detail ->
                            entities.find { it.id == detail.id }?.toExpenseDetailUi()
                        }
                    state.copy(
                        todayExpenses = snapshot.todayExpenseItems,
                        historyGroups = snapshot.historyGroups,
                        spentTodayRupees = snapshot.spentTodayRupees,
                        transactionCount = snapshot.transactionCountToday,
                        monthSpentRupees = snapshot.monthSpentRupees,
                        weeklySpentRupees = snapshot.weeklySpentRupees,
                        categoryBreakdown = snapshot.categoryBreakdown,
                        weekDailyBars = snapshot.weekDailyBars,
                        remainingRupees = remaining,
                        budgetUsedPercent = usedPercent,
                        selectedExpenseDetail = refreshedDetail,
                        isExpensesLoading = false,
                    )
                }
            }
        }
    }

    override fun onActive() {
        _uiState.update {
            it.copy(hasNotificationAccess = notificationAccessManager.hasNotificationAccess())
        }
        refreshSavedDailyLimit()
    }

    override fun onTabSelected(tab: DashboardTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    override fun onSeeAllExpensesClick() {
        // "See all" on Home jumps to the History tab — same dashboard, just
        // another tab, so we reuse the existing tab-selection path.
        onTabSelected(DashboardTab.History)
    }

    override fun onFabClick() {
        _uiState.update { it.copy(showAddExpenseSheet = true) }
    }

    override fun onDismissAddExpenseSheet() {
        _uiState.update {
            it.copy(showAddExpenseSheet = false, expenseEditDraft = null)
        }
    }

    override fun onExpenseClick(expenseId: Long) {
        val entity = cachedExpenses.find { it.id == expenseId }
            ?: return
        _uiState.update { it.copy(selectedExpenseDetail = entity.toExpenseDetailUi()) }
    }

    override fun onDismissExpenseDetail() {
        _uiState.update { it.copy(selectedExpenseDetail = null) }
    }

    override fun onDeleteExpense(expenseId: Long) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expenseId)
            _uiState.update { it.copy(selectedExpenseDetail = null) }
        }
    }

    override fun onEditExpenseClick(expenseId: Long) {
        val detail = _uiState.value.selectedExpenseDetail
            ?: return
        if (detail.id != expenseId) return
        _uiState.update {
            it.copy(
                selectedExpenseDetail = null,
                showAddExpenseSheet = true,
                expenseEditDraft =
                    ExpenseEditDraft(
                        expenseId = detail.id,
                        amountRupees = detail.amountRupees,
                        title = detail.title,
                        note = detail.note,
                        category = detail.iconStyle,
                    ),
            )
        }
    }

    override fun onSaveManualExpense(
        amountRupees: Int,
        title: String,
        note: String,
        category: ExpenseIconStyle,
    ) {
        if (amountRupees <= 0) return
        val trimmedTitle = title.ifBlank { "Expense" }
        val trimmedNote = note.trim()
        val editDraft = _uiState.value.expenseEditDraft
        viewModelScope.launch {
            if (editDraft != null) {
                expenseRepository.updateExpense(
                    id = editDraft.expenseId,
                    title = trimmedTitle,
                    amountRupees = amountRupees,
                    note = trimmedNote,
                    category = category,
                )
            } else {
                expenseRepository.insertManualExpense(
                    title = trimmedTitle,
                    amountRupees = amountRupees,
                    note = trimmedNote,
                    category = category,
                )
            }
            _uiState.update {
                it.copy(showAddExpenseSheet = false, expenseEditDraft = null)
            }
        }
    }

    override fun onAlertThresholdSelect(threshold: AlertThreshold) {
        _uiState.update { it.copy(alertThreshold = threshold) }
    }

    override fun onGrantNotificationAccessClick(activityContext: Context) {
        notificationAccessManager.openNotificationAccessSettings(activityContext)
    }

    override fun onNotificationsClick() {
        viewModelScope.launch {
            navigator.navigate(Destination.NotificationsSettings)
        }
    }

    override fun onLogoutClick() {
        // Sign out from Firebase first, then bounce back to the auth graph
        // and tear down the dashboard graph entirely so the user can't swipe
        // back into a "logged-out" Home. AuthGraph's start destination
        // (GetStarted) becomes the new top.
        viewModelScope.launch {
            clearBudgetSetup()
            logoutUseCase()
            navigator.navigate(Destination.AuthGraph) {
                popUpTo(Destination.DashboardGraph) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    companion object {
        internal fun greetingPrefixForHour(calendar: Calendar): String {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            return when (hour) {
                in 5..11 -> "Good morning,"
                in 12..16 -> "Good afternoon,"
                else -> "Good evening,"
            }
        }
    }

    private fun currentUserDisplayName(): String {
        val user = authRepository.currentUser
        return user?.displayName
            ?.takeIf { it.isNotBlank() }
            ?: user?.email
                ?.substringBefore("@")
                ?.takeIf { it.isNotBlank() }
            ?: HomeUiState.Initial.userName
    }

    private fun refreshSavedDailyLimit() {
        viewModelScope.launch {
            val savedLimit = getDailyLimit() ?: return@launch
            _uiState.update { state ->
                val remaining = (savedLimit - state.spentTodayRupees).coerceAtLeast(0)
                val usedPercent =
                    if (savedLimit > 0) {
                        ((state.spentTodayRupees * 100f) / savedLimit).toInt().coerceAtLeast(0)
                    } else {
                        0
                    }
                state.copy(
                    dailyLimitRupees = savedLimit,
                    remainingRupees = remaining,
                    budgetUsedPercent = usedPercent,
                )
            }
        }
    }
}
