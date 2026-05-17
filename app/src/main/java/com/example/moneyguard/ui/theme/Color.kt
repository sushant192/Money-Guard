package com.example.moneyguard.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Brand palette
val BrandBlue = Color(0xFF2563EB)        // primary brand blue
val BrandBlueDeep = Color(0xFF1E40AF)

// Brand palette — match prototype exactly
val BrandBlueStart = Color(0xFF1565C0)   // deep blue
val BrandBlueMid   = Color(0xFF1976D2)   // mid blue
val BrandBlueEnd   = Color(0xFF2196F3)   // light blue accent

// Diagonal gradient brush (reuse wherever needed)
val BrandGradient = Brush.linearGradient(
    colorStops = arrayOf(
        0.00f to BrandBlueDeep,
        0.55f to BrandBlueMid,
        1.00f to BrandBlueEnd
    )
)

// Form / surface tokens
val FieldBackground = Color(0xFFEEF1FA)        // soft blue-grey for text field background
val FieldLabelText  = Color(0xFF6B7280)        // muted label / helper text
val MutedText       = Color(0xFF6B7280)        // body / secondary text on white

/** Error — clean, vibrant red (Material red-700) used for borders, labels, and helper text. */
val ErrorMain = Color(0xFFD32F2F)

/** Error on dark backgrounds — readable accent (M3-style). */
val ErrorOnDark = Color(0xFFFFB4AB)

/** Home dashboard header — matches primary marketing / prototype blue. */
val HomeHeaderBlue = Color(0xFF1E75D5)

/** Home header background — same blue family as [BrandGradient] on the set-limit flow. */
val HomeHeaderBlueGradient =
    Brush.verticalGradient(
        colorStops =
            arrayOf(
                0.00f to BrandBlueStart,
                0.50f to BrandBlueMid,
                1.00f to HomeHeaderBlue,
            ),
    )

/** Home header when daily spend is over the limit. */
val HomeHeaderOverLimitGradient =
    Brush.verticalGradient(
        colorStops =
            arrayOf(
                0.00f to Color(0xFF7A1515),
                0.45f to Color(0xFFC62828),
                1.00f to Color(0xFFE53935),
            ),
    )

/** Solid tone at the bottom of the sticky greeting strip — matches gradient top stop. */
val HomeStickyHeaderBlue = BrandBlueStart

val HomeStickyHeaderOverLimit = Color(0xFF7A1515)