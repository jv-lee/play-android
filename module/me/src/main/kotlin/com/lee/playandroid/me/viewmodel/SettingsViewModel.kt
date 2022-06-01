package com.lee.playandroid.me.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.base.ApplicationExtensions.app
import com.lee.playandroid.base.utils.CacheUtil
import com.lee.playandroid.service.AccountService
import com.lee.playandroid.service.hepler.ModuleService
import com.lee.playandroid.me.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 *
 * @author jv.lee
 * @date 2022/4/29
 */
class SettingsViewModel : ViewModel() {

    var accountService = ModuleService.find<AccountService>()

    private val _viewStates = MutableStateFlow(SettingsViewState())
    val viewStates: StateFlow<SettingsViewState> = _viewStates

    private val _viewEvents = Channel<SettingsViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    init {
        initCacheSize()
    }

    fun dispatch(action: SettingsViewAction) {
        when (action) {
            is SettingsViewAction.VisibleCacheDialog -> {
                visibleCacheDialog(action.visibility)
            }
            is SettingsViewAction.VisibleLogoutDialog -> {
                visibleLogoutDialog(action.visibility)
            }
            is SettingsViewAction.RequestClearCache -> {
                requestClearCache()
            }
        }
    }

    private fun initCacheSize() {
        viewModelScope.launch {
            val totalCacheSize = CacheUtil.getTotalCacheSize(app)
            _viewStates.update { it.copy(totalCacheSize = totalCacheSize) }
        }
    }

    private fun visibleCacheDialog(visibility: Boolean) {
        viewModelScope.launch {
            _viewStates.update { it.copy(isCacheConfirm = visibility) }
        }
    }

    private fun visibleLogoutDialog(visibility: Boolean) {
        viewModelScope.launch {
            _viewStates.update { it.copy(isLogoutConfirm = visibility) }
        }
    }

    private fun requestClearCache() {
        viewModelScope.launch {
            flow {
                emit(CacheUtil.clearAllCache(app))
            }.onStart {
                _viewStates.update { it.copy(isLoading = true) }
            }.catch {
                _viewStates.update { it.copy(isLoading = false) }
            }.collect { isSuccess ->
                val message =
                    app.getString(if (isSuccess) R.string.settings_clear_success else R.string.settings_clear_failed)
                _viewEvents.send(SettingsViewEvent.ClearCacheResult(message = message))
                _viewStates.update { it.copy(isLoading = false, isCacheConfirm = false) }
                initCacheSize()
            }
        }
    }

}

data class SettingsViewState(
    val isLoading: Boolean = false,
    val isCacheConfirm: Boolean = false,
    val isLogoutConfirm: Boolean = false,
    val totalCacheSize: String = "",
)

sealed class SettingsViewEvent {
    data class ClearCacheResult(val message: String) : SettingsViewEvent()
}

sealed class SettingsViewAction {
    data class VisibleCacheDialog(val visibility: Boolean) : SettingsViewAction()
    data class VisibleLogoutDialog(val visibility: Boolean) : SettingsViewAction()
    object RequestClearCache : SettingsViewAction()
}