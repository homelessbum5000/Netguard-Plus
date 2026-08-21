package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CyberEmeraldPrimary,
    secondary = CyberPrimaryCyan,
    tertiary = CyberSecondaryEmerald,
    background = CyberDarkBg,
    surface = CyberCardBg,
    onPrimary = CyberDarkBg,
    onSecondary = CyberDarkBg,
    onBackground = CyberTextPrimary,
    onSurface = CyberTextPrimary,
    error = CyberAlertRed
)

@Composable
fun GrapheneGuardTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
