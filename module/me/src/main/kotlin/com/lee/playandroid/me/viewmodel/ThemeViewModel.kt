package com.lee.playandroid.me.viewmodel

import androidx.lifecycle.ViewModel
import com.lee.playandroid.base.tools.DarkModeTools
import com.lee.playandroid.base.tools.DarkViewUpdateTools
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * app主题控制viewModel
 * @author jv.lee
 * @date 2022/4/20
 */
class ThemeViewModel : ViewModel() {

    private val darkModeTools: DarkModeTools = DarkModeTools.get()

    private val _viewStates: MutableStateFlow<ThemeViewState> = MutableStateFlow(ThemeViewState())
    val viewStates: StateFlow<ThemeViewState> = _viewStates

    init {
        initDarkTheme()
    }

    fun dispatch(intent: ThemeViewIntent) {
        when (intent) {
            is ThemeViewIntent.UpdateDarkStatus -> {
                updateDark(intent.enable)
            }
            is ThemeViewIntent.UpdateSystemStatus -> {
                updateSystem(intent.enable)
            }
            is ThemeViewIntent.ResetThemeStatus -> {
                initDarkTheme()
            }
        }
    }

    private fun updateDark(enable: Boolean) {
        // 过滤防止跟随系统选择时重复选中
        if (viewStates.value.isDark != enable) {
            darkModeTools.updateDarkTheme(enable = enable)
            DarkViewUpdateTools.notifyUiMode()
            initDarkTheme()
        }
    }

    private fun updateSystem(enable: Boolean) {
        darkModeTools.updateSystemTheme(enable = enable)
        DarkViewUpdateTools.notifyUiMode()
        initDarkTheme()
    }

    private fun initDarkTheme() {
        val isDark = darkModeTools.isDarkTheme()
        val isSystem = darkModeTools.isSystemTheme()
        _viewStates.update {
            it.copy(
                isDark = isDark,
                isSystem = isSystem,
                statusBarDarkContentEnabled = !isDark
            )
        }
    }
}

data class ThemeViewState(
    val isDark: Boolean = false,
    val isSystem: Boolean = false,
    val statusBarDarkContentEnabled: Boolean = false
)

sealed class ThemeViewIntent {
    data class UpdateDarkStatus(val enable: Boolean) : ThemeViewIntent()
    data class UpdateSystemStatus(val enable: Boolean) : ThemeViewIntent()
    object ResetThemeStatus : ThemeViewIntent()
}