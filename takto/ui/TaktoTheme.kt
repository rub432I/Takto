package com.takto.launcher.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Takto Visual Architecture:
 * Pure OLED true-black (#000000), stark white (#FFFFFF), signature warm amber accent (#FFC86B / #DF9E38),
 * and brutalist Greycliff CF typography.
 */

val OledBlack = Color(0xFF000000)
val SurfaceDark = Color(0xFF080A0E)
val CardSurface = Color(0xFF0E1218)
val AccentGold = Color(0xFFFFC86B)
val AccentAmber = Color(0xFFDF9E38)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA0A0A0)
val TextMuted = Color(0xFF555555)
val BorderSubtle = Color(0x33DF9E38)

val TaktoDarkColorScheme: ColorScheme = darkColorScheme(
    primary = AccentGold,
    onPrimary = OledBlack,
    primaryContainer = AccentAmber,
    onPrimaryContainer = OledBlack,
    surface = OledBlack,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = TextSecondary,
    background = OledBlack,
    onBackground = TextPrimary,
    outline = BorderSubtle
)

@Composable
fun TaktoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TaktoDarkColorScheme,
        content = content
    )
}
