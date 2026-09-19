package com.wayss.app.ui.components

import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.wayss.app.viewmodel.VisualStyle

@Composable
fun WayssIcon(name: String, style: VisualStyle, active: Boolean) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed || active) 1.12f else 1f,
        animationSpec = spring(),
        label = "wayss-icon"
    )
    val symbol = when (name) {
        "home" -> "⌂"
        "search" -> "⌕"
        "create" -> "＋"
        "messages" -> "◈"
        "profile" -> "◉"
        "settings" -> "⚙"
        "verified" -> "◆"
        else -> "•"
    }
    Text(
        text = symbol,
        modifier = Modifier.size(28.dp).scale(scale)
    )
}
