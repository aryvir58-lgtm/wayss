package com.wayss.app.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.wayss.app.ui.components.WayssIcon
import com.wayss.app.ui.screens.*
import com.wayss.app.viewmodel.AppViewModel
import com.wayss.app.viewmodel.VisualStyle

@Composable
fun WayssApp(vm: AppViewModel) {
    val nav = rememberNavController()
    val style by vm.visualStyle.collectAsState()
    val dark by vm.darkMode.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    "home" to "Home",
                    "search" to "Search",
                    "create" to "Create",
                    "messages" to "Messages",
                    "profile" to "Profile"
                )
                items.forEach { (route, label) ->
                    NavigationBarItem(
                        selected = false,
                        onClick = { nav.navigate(route) },
                        icon = { WayssIcon(route, style, active = false) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(nav, startDestination = "home", modifier = Modifier.padding(padding)) {
            composable("home") { HomeScreen(style) }
            composable("search") { SearchScreen(style) }
            composable("create") { CreateScreen(style) }
            composable("messages") { MessagesScreen(style) }
            composable("profile") { ProfileScreen(style, vm) }
            composable("settings") { SettingsScreen(style, dark, vm) }
        }
    }
}
