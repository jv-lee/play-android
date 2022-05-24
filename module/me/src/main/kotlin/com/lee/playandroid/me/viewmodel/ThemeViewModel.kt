package com.lee.playandroid.me.viewmodel

import androidx.lifecycle.ViewModel
import com.lee.library.tools.DarkModeTools
import com.lee.library.tools.DarkViewUpdateTools
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * @author jv.lee
 * @date 2022/4/20
 * @description app主题控制viewModel
 */
class ThemeViewModel : ViewModel() {

    private val darkModeTools: DarkModeTools = DarkModeTools.get()

    private val _viewStates: MutableStateFlow<ThemeViewState> = MutableStateFlow(ThemeViewState())
    val viewStates: StateFlow<ThemeViewState> = _viewStates

    init {
        initDarkTheme()
    }

    fun dispatch(action: ThemeViewAction) {
        when (action) {
            is ThemeViewAction.UpdateDarkAction -> {
                updateDark(action.enable)
            }
            is ThemeViewAction.UpdateSystemAction -> {
                updateSystem(action.enable)
            }
            is ThemeViewAction.ResetThemeStatus -> {
                initDarkTheme()
            }
        }
    }

    private fun updateDark(enable: Boolean) {
        // 过滤防止跟随系统选择时重复选中
        if (viewStates.value.isDark != enable) {
            darkModeTools.updateNightTheme(enable = enable)
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
                isDark = isDark, isSystem = isSystem,
                statusBarDarkContentEnabled = !isDark
            )
        }
    }

}

data class ThemeViewState(
    val isDark: Boolean = false,
    val isSystem: Boolean = false,
    val statusBarDarkContentEnabled: Boolean = false,
)

sealed class ThemeViewAction {
    data class UpdateDarkAction(val enable: Boolean) : ThemeViewAction()
    data class UpdateSystemAction(val enable: Boolean) : ThemeViewAction()
    object ResetThemeStatus : ThemeViewAction()
}