package com.wayss.app.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.wayss.app.viewmodel.VisualStyle

private val Dark = darkColorScheme(
    primary = Color(0xFF9B83FF),
    secondary = Color(0xFF63D8FF),
    background = Color(0xFF0B0B10),
    surface = Color(0xFF15151D)
)

private val Light = lightColorScheme(
    primary = Color(0xFF6848D8),
    secondary = Color(0xFF167C9B),
    background = Color(0xFFF7F6FA),
    surface = Color.White
)

@Composable
fun WayssTheme(
    dark: Boolean,
    visualStyle: VisualStyle,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (dark) Dark else Light,
        typography = Typography(),
        content = content
    )
}
