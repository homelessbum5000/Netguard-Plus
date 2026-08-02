package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyberEmeraldPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = CyberEmeraldPrimary,
    secondary = CyberEmeraldVariant,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF022C22),
    onSecondaryContainer = CyberEmeraldVariant,
    tertiary = CyberPrimaryCyan,
    onTertiary = Color.Black,
    background = CyberDarkBg,
    onBackground = CyberTextPrimary,
    surface = CyberCardBg,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberCardBorder,
    onSurfaceVariant = CyberTextSecondary,
    error = CyberAlertRed,
    onError = Color.White
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

