package com.example.tsumap.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TsuColorScheme = lightColorScheme(
    primary = Color(0xFF003087),
    onPrimary = Color.White,
    secondary = Color(0xFF0055A5),
    onSecondary = Color.White,
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1C1C),
    onSurface = Color(0xFF1C1C1C)
)

@Composable
fun TsuMapTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TsuColorScheme,
        content = content,
        typography = TsuTypography
    )
}
