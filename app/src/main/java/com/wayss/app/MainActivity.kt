package com.wayss.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wayss.app.ui.WayssApp
import com.wayss.app.ui.theme.WayssTheme
import com.wayss.app.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: AppViewModel = viewModel()
            WayssTheme(
                dark = vm.darkMode.collectAsState().value,
                visualStyle = vm.visualStyle.collectAsState().value
            ) {
                WayssApp(vm)
            }
        }
    }
}
