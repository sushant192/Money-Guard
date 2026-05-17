package com.example.moneyguard.features.dashboard.home.data

import androidx.compose.ui.graphics.Color
import com.example.moneyguard.R
import com.example.moneyguard.data.local.entity.ExpenseEntity
import com.example.moneyguard.features.dashboard.home.ui.BarKind
import com.example.moneyguard.features.dashboard.home.ui.CategorySpendUi
import com.example.moneyguard.features.dashboard.home.ui.DayBarUi
import com.example.moneyguard.features.dashboard.home.ui.ExpenseIconStyle
import com.example.moneyguard.features.dashboard.home.ui.ExpenseItemUi
import com.example.moneyguard.features.dashboard.home.ui.HistoryGroup
import com.example.moneyguard.features.dashboard.home.ui.HistoryItemUi
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val DAY_MS = 24L * 60L * 60L * 1000L

/**
 * Aggregates persisted expenses into the Home / History / Stats fields that
 * [com.example.moneyguard.features.dashboard.home.ui.HomeViewModel] keeps in state.
 */
data class HomeExpenseSnapshot(
    val todayExpenseItems: List<ExpenseItemUi>,
    val historyGroups: List<HistoryGroup>,
    val spentTodayRupees: Int,
    val transactionCountToday: Int,
    val monthSpentRupees: Int,
    val weeklySpentRupees: Int,
    val categoryBreakdown: List<CategorySpendUi>,
    val weekDailyBars: List<DayBarUi>,
) {
    companion object {

        fun from(entities: List<ExpenseEntity>, now: Calendar): HomeExpenseSnapshot {
            if (entities.isEmpty()) {
                return HomeExpenseSnapshot(
                    todayExpenseItems = emptyList(),
                    historyGroups = emptyList(),
                    spentTodayRupees = 0,
                    transactionCountToday = 0,
                    monthSpentRupees = 0,
                    weeklySpentRupees = 0,
                    categoryBreakdown = emptyList(),
                    weekDailyBars = emptyWeekBars(now),
                )
            }

            val todayStart = startOfDayMillis(now)
            val todayEnd = todayStart + DAY_MS

            val inToday = entities.filter { it.createdAtEpochMs in todayStart until todayEnd }
            val spentToday = inToday.sumOf { it.amountRupees }
            val todayItems = inToday
                .sortedByDescending { it.createdAtEpochMs }
                .map { it.toExpenseItemUi() }

            val monthStart = startOfMonthMillis(now)
            val nextMonthStart = startOfNextMonthMillis(now)
            val inMonth = entities.filter { it.createdAtEpochMs in monthStart until nextMonthStart }
            val monthSpent = inMonth.sumOf { it.amountRupees }

            val weekStart = startOfWeekMondayMillis(now)
            val weekEnd = weekStart + 7 * DAY_MS
            val inWeek = entities.filter { it.createdAtEpochMs in weekStart until weekEnd }
            val weekSpent = inWeek.sumOf { it.amountRupees }

            return HomeExpenseSnapshot(
                todayExpenseItems = todayItems,
                historyGroups = entities.toHistoryGroups(now),
                spentTodayRupees = spentToday,
                transactionCountToday = inToday.size,
                monthSpentRupees = monthSpent,
                weeklySpentRupees = weekSpent,
                categoryBreakdown = inMonth.toCategoryBreakdown(),
                weekDailyBars = entities.toWeekDailyBars(now),
            )
        }
    }
}

private fun emptyWeekBars(now: Calendar): List<DayBarUi> {
    val weekStart = startOfWeekMondayMillis(now)
    val weekCal = Calendar.getInstance(now.timeZone, Locale.getDefault()).apply {
        timeInMillis = weekStart
    }
    val labels = SimpleDateFormat("EEE", Locale.getDefault())
    return (0 until 7).map { offset ->
        val c = (weekCal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, offset) }
        val isToday = isSameDay(c, now)
        DayBarUi(
            label = labels.format(c.time),
            heightFraction = 0f,
            kind = when {
                isToday -> BarKind.Today
                c.before(truncateToStartOfDay(now)) -> BarKind.Past
                else -> BarKind.Future
            },
        )
    }
}

private fun List<ExpenseEntity>.toWeekDailyBars(now: Calendar): List<DayBarUi> {
    val weekStart = startOfWeekMondayMillis(now)
    val dayLabels = SimpleDateFormat("EEE", Locale.getDefault())
    val amountsByDay = LongArray(7)
    val weekCal = Calendar.getInstance(now.timeZone, Locale.getDefault()).apply {
        timeInMillis = weekStart
    }
    for (entity in this) {
        val dayIndex = ((entity.createdAtEpochMs - weekStart) / DAY_MS).toInt()
        if (dayIndex in 0..6) {
            amountsByDay[dayIndex] += entity.amountRupees
        }
    }
    val maxAmount = amountsByDay.maxOrNull()?.coerceAtLeast(1) ?: 1
    return (0 until 7).map { offset ->
        val c = (weekCal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, offset) }
        val amount = amountsByDay[offset].toInt()
        val isToday = isSameDay(c, now)
        DayBarUi(
            label = dayLabels.format(c.time),
            heightFraction = (amount.toFloat() / maxAmount.toFloat()).coerceIn(0f, 1f),
            kind = when {
                isToday -> BarKind.Today
                c.before(truncateToStartOfDay(now)) -> BarKind.Past
                else -> BarKind.Future
            },
        )
    }
}

private fun List<ExpenseEntity>.toHistoryGroups(now: Calendar): List<HistoryGroup> {
    val byDay = groupBy { startOfDayMillisForTimestamp(it.createdAtEpochMs, now) }
        .toSortedMap(compareByDescending { it })
    return byDay.map { (dayStart, rows) ->
        HistoryGroup(
            title = dayBucketTitle(dayStart, now),
            items = rows
                .sortedByDescending { it.createdAtEpochMs }
                .map { it.toHistoryItemUi() },
        )
    }
}

private fun List<ExpenseEntity>.toCategoryBreakdown(): List<CategorySpendUi> {
    if (isEmpty()) return emptyList()
    return groupBy { it.category.toExpenseIconStyle() }
        .map { (style, list) ->
            val meta = categorySpendMeta(style)
            CategorySpendUi(
                nameRes = meta.first,
                amountRupees = list.sumOf { it.amountRupees },
                color = meta.second,
            )
        }
        .filter { it.amountRupees > 0 }
        .sortedByDescending { it.amountRupees }
}

private fun categorySpendMeta(style: ExpenseIconStyle): Pair<Int, Color> = when (style) {
    ExpenseIconStyle.Food -> R.string.category_food to Color(0xFF2563EB)
    ExpenseIconStyle.Entertainment -> R.string.category_entertainment to Color(0xFF7C4DFF)
    ExpenseIconStyle.Transfer -> R.string.category_transfer to Color(0xFF2E7D32)
    ExpenseIconStyle.Travel -> R.string.category_travel to Color(0xFF8E24AA)
    ExpenseIconStyle.Bills -> R.string.category_bills to Color(0xFFEF6C00)
}

private fun String.toExpenseIconStyle(): ExpenseIconStyle =
    ExpenseIconStyle.entries.firstOrNull { it.name == this } ?: ExpenseIconStyle.Food

private fun categoryMetaLabel(category: ExpenseIconStyle): String = when (category) {
    ExpenseIconStyle.Entertainment -> "Entertainment"
    ExpenseIconStyle.Food -> "Food"
    ExpenseIconStyle.Transfer -> "Transfer"
    ExpenseIconStyle.Bills -> "Bills"
    ExpenseIconStyle.Travel -> "Travel"
}

private fun ExpenseEntity.toExpenseItemUi(): ExpenseItemUi {
    val style = category.toExpenseIconStyle()
    val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(createdAtEpochMs))
    val isFromNotification = sourceKey != null

    val displayTitle =
        when {
            isFromNotification && !title.startsWith("UPI to ", ignoreCase = true) ->
                "UPI to ${title.removePrefix("Payment").trim().ifBlank { title }}"
            else -> title
        }

    val metaLine = "${categoryMetaLabel(style)} · $timeStr"

    val paymentLabel =
        when {
            paymentSource.equals("Manual", ignoreCase = true) -> paymentSource
            isFromNotification -> "UPI"
            paymentSource.equals("UPI", ignoreCase = true) -> "UPI"
            paymentSource.equals("Card", ignoreCase = true) -> "Card"
            else -> "UPI"
        }

    return ExpenseItemUi(
        title = displayTitle,
        metaLine = metaLine,
        amountRupees = amountRupees,
        paymentLabel = paymentLabel,
        iconStyle = style,
    )
}

private fun ExpenseEntity.toHistoryItemUi(): HistoryItemUi {
    val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(createdAtEpochMs))
    return HistoryItemUi(
        title = title,
        time = timeStr,
        amountRupees = amountRupees,
        iconStyle = category.toExpenseIconStyle(),
    )
}

private fun dayBucketTitle(dayStartMs: Long, now: Calendar): String {
    val todayStart = startOfDayMillis(now)
    val yesterdayStart = todayStart - DAY_MS
    return when (dayStartMs) {
        todayStart -> "Today"
        yesterdayStart -> "Yesterday"
        else -> {
            val pattern = if (yearOf(dayStartMs, now) == now.get(Calendar.YEAR)) {
                "d MMM"
            } else {
                "d MMM yyyy"
            }
            SimpleDateFormat(pattern, Locale.getDefault()).format(Date(dayStartMs))
        }
    }
}

private fun yearOf(epoch: Long, tzTemplate: Calendar): Int {
    val c = Calendar.getInstance(tzTemplate.timeZone, Locale.getDefault())
    c.timeInMillis = epoch
    return c.get(Calendar.YEAR)
}

private fun startOfDayMillisForTimestamp(epoch: Long, tzTemplate: Calendar): Long {
    val c = Calendar.getInstance(tzTemplate.timeZone, Locale.getDefault())
    c.timeInMillis = epoch
    truncateToStartOfDayInPlace(c)
    return c.timeInMillis
}

private fun truncateToStartOfDay(now: Calendar): Calendar {
    val c = (now.clone() as Calendar)
    truncateToStartOfDayInPlace(c)
    return c
}

private fun truncateToStartOfDayInPlace(c: Calendar) {
    c.set(Calendar.HOUR_OF_DAY, 0)
    c.set(Calendar.MINUTE, 0)
    c.set(Calendar.SECOND, 0)
    c.set(Calendar.MILLISECOND, 0)
}

private fun startOfDayMillis(now: Calendar): Long =
    truncateToStartOfDay(now).timeInMillis

private fun isSameDay(a: Calendar, b: Calendar): Boolean =
    a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
        a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

private fun startOfMonthMillis(now: Calendar): Long {
    val c = truncateToStartOfDay(now)
    c.set(Calendar.DAY_OF_MONTH, 1)
    return c.timeInMillis
}

private fun startOfNextMonthMillis(now: Calendar): Long {
    val c = truncateToStartOfDay(now)
    c.set(Calendar.DAY_OF_MONTH, 1)
    c.add(Calendar.MONTH, 1)
    return c.timeInMillis
}

private fun startOfWeekMondayMillis(now: Calendar): Long {
    val c = truncateToStartOfDay(now)
    val dayOffset = (c.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7
    c.add(Calendar.DAY_OF_MONTH, -dayOffset)
    return c.timeInMillis
}
