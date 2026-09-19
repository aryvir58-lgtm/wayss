package com.wayss.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wayss.app.model.WayssUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class VisualStyle { THREE_D, NORMAL }

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences("wayss", 0)

    private val _darkMode = MutableStateFlow(prefs.getBoolean("dark", true))
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()

    private val _visualStyle = MutableStateFlow(
        if (prefs.getString("visual", "3d") == "3d") VisualStyle.THREE_D else VisualStyle.NORMAL
    )
    val visualStyle: StateFlow<VisualStyle> = _visualStyle.asStateFlow()

    private val _currentUser = MutableStateFlow<WayssUser?>(null)
    val currentUser: StateFlow<WayssUser?> = _currentUser.asStateFlow()

    fun setDark(value: Boolean) {
        _darkMode.value = value
        prefs.edit().putBoolean("dark", value).apply()
    }

    fun setVisualStyle(value: VisualStyle) {
        _visualStyle.value = value
        prefs.edit().putString("visual", if (value == VisualStyle.THREE_D) "3d" else "normal").apply()
    }

    fun setUser(user: WayssUser?) {
        viewModelScope.launch { _currentUser.emit(user) }
    }
}
