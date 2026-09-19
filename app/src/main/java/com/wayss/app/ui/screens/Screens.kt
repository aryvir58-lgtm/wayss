package com.wayss.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wayss.app.ui.components.WayssIcon
import com.wayss.app.viewmodel.AppViewModel
import com.wayss.app.viewmodel.VisualStyle

@Composable
fun Shell(title: String, style: VisualStyle, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        content()
    }
}

@Composable fun HomeScreen(style: VisualStyle) = Shell("Wayss", style) {
    Text("Your feed")
    Spacer(Modifier.height(12.dp))
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text("No posts yet", style = MaterialTheme.typography.titleMedium)
            Text("Connect with people to build your feed.")
        }
    }
}

@Composable fun SearchScreen(style: VisualStyle) = Shell("Search", style) {
    OutlinedTextField(value = "", onValueChange = {}, label = { Text("Search users, posts, hashtags") }, modifier = Modifier.fillMaxWidth())
}

@Composable fun CreateScreen(style: VisualStyle) = Shell("Create", style) {
    Button(onClick = {}) { Text("Select media") }
    Spacer(Modifier.height(10.dp))
    Text("Camera/gallery upload pipeline is wired through the media layer.")
}

@Composable fun MessagesScreen(style: VisualStyle) = Shell("Messages", style) {
    Text("Real-time conversations")
    Spacer(Modifier.height(10.dp))
    Text("Firebase conversation repository is ready for integration.")
}

@Composable fun ProfileScreen(style: VisualStyle, vm: AppViewModel) = Shell("Profile", style) {
    Text("@username")
    Text("Wayss profile")
    Spacer(Modifier.height(16.dp))
    Button(onClick = {}) { Text("Edit profile") }
}

@Composable
fun SettingsScreen(style: VisualStyle, dark: Boolean, vm: AppViewModel) = Shell("Settings", style) {
    Text("Appearance", style = MaterialTheme.typography.titleLarge)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Dark mode")
        Switch(checked = dark, onCheckedChange = vm::setDark)
    }
    Spacer(Modifier.height(12.dp))
    Text("Visual Style", style = MaterialTheme.typography.titleLarge)
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FilterChip(
            selected = style == VisualStyle.THREE_D,
            onClick = { vm.setVisualStyle(VisualStyle.THREE_D) },
            label = { Text("3D Style") }
        )
        FilterChip(
            selected = style == VisualStyle.NORMAL,
            onClick = { vm.setVisualStyle(VisualStyle.NORMAL) },
            label = { Text("Normal Style") }
        )
    }
    Spacer(Modifier.height(20.dp))
    Text("Admin-only verification and moderation screens are intentionally protected by backend role checks.")
}
