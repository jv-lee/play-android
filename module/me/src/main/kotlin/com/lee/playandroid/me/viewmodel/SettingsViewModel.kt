package com.lee.playandroid.me.viewmodel

import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.base.ApplicationExtensions.app
import com.lee.playandroid.base.utils.CacheUtil
import com.lee.playandroid.base.viewmodel.BaseMVIViewModel
import com.lee.playandroid.base.viewmodel.IViewEvent
import com.lee.playandroid.base.viewmodel.IViewIntent
import com.lee.playandroid.base.viewmodel.IViewState
import com.lee.playandroid.me.R
import com.lee.playandroid.service.AccountService
import com.lee.playandroid.service.hepler.ModuleService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 设置页viewModel
 * @author jv.lee
 * @date 2022/4/29
 */
class SettingsViewModel :
    BaseMVIViewModel<SettingsViewState, SettingsViewEvent, SettingsViewIntent>() {

    var accountService = ModuleService.find<AccountService>()

    init {
        initCacheSize()
    }

    override fun initViewState() = SettingsViewState()

    override fun dispatch(intent: SettingsViewIntent) {
        when (intent) {
            is SettingsViewIntent.VisibleCacheDialog -> {
                visibleCacheDialog(intent.visibility)
            }

            is SettingsViewIntent.VisibleLogoutDialog -> {
                visibleLogoutDialog(intent.visibility)
            }

            is SettingsViewIntent.RequestClearCache -> {
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
                    app.getString(
                        if (isSuccess) R.string.settings_clear_success
                        else R.string.settings_clear_failed
                    )
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
    val totalCacheSize: String = ""
) : IViewState

sealed class SettingsViewEvent : IViewEvent {
    data class ClearCacheResult(val message: String) : SettingsViewEvent()
}

sealed class SettingsViewIntent : IViewIntent {
    data class VisibleCacheDialog(val visibility: Boolean) : SettingsViewIntent()
    data class VisibleLogoutDialog(val visibility: Boolean) : SettingsViewIntent()
    object RequestClearCache : SettingsViewIntent()
}