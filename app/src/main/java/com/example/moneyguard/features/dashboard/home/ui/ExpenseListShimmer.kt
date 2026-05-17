package com.example.moneyguard.features.dashboard.home.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.moneyguard.ui.theme.FieldBackground

@Composable
fun TodayExpensesShimmer(
    itemCount: Int = 3,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        repeat(itemCount) { index ->
            ExpenseRowShimmer()
            if (index < itemCount - 1) {
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ExpenseRowShimmer() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(FieldBackground)
                .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShimmerPlaceholder(
            modifier =
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp)),
        )
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
        ) {
            ShimmerPlaceholder(
                modifier =
                    Modifier
                        .fillMaxWidth(0.58f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(6.dp)),
            )
            Spacer(Modifier.height(8.dp))
            ShimmerPlaceholder(
                modifier =
                    Modifier
                        .fillMaxWidth(0.38f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            ShimmerPlaceholder(
                modifier =
                    Modifier
                        .width(52.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(6.dp)),
            )
            Spacer(Modifier.height(8.dp))
            ShimmerPlaceholder(
                modifier =
                    Modifier
                        .width(28.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
            )
        }
    }
}

@Composable
private fun ShimmerPlaceholder(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "expense_shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.92f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 850, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "shimmer_alpha",
    )
    Spacer(
        modifier =
            modifier.background(Color(0xFFDCE3F0).copy(alpha = alpha)),
    )
}
