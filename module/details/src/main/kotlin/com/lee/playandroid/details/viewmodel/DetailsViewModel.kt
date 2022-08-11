package com.lee.playandroid.details.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.base.ApplicationExtensions.app
import com.lee.playandroid.base.extensions.lowestTime
import com.lee.playandroid.common.constants.ApiConstants
import com.lee.playandroid.details.R
import com.lee.playandroid.details.ui.DetailsFragment.Companion.ARG_PARAMS_COLLECT
import com.lee.playandroid.details.ui.DetailsFragment.Companion.ARG_PARAMS_ID
import com.lee.playandroid.details.ui.DetailsFragment.Companion.ARG_PARAMS_TITLE
import com.lee.playandroid.details.ui.DetailsFragment.Companion.ARG_PARAMS_URL
import com.lee.playandroid.service.MeService
import com.lee.playandroid.service.hepler.ModuleService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 文章详情页viewModel
 * @param savedStateHandle 页面状态（透传参数）
 * @author jv.lee
 * @date 2021/12/3
 */
class DetailsViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val params = DetailsParams(savedStateHandle)

    private val meService = ModuleService.find<MeService>()

    private var _viewStates =
        MutableStateFlow(DetailsViewState(title = params.title, actionEnable = params.id != 0L))
    val viewStates: StateFlow<DetailsViewState> = _viewStates

    private val _viewEvents = Channel<DetailsViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: DetailsViewAction) {
        when (action) {
            is DetailsViewAction.UpdateCollectStatus -> {
                requestCollect()
            }
            is DetailsViewAction.RequestShareDetails -> {
                requestShareDetails()
            }
        }
    }

    private fun requestShareDetails() {
        viewModelScope.launch {
            _viewEvents.send(DetailsViewEvent.ShareEvent("${params.title}:${params.url}"))
        }
    }

    private fun requestCollect() {
        viewModelScope.launch {
            //已收藏直接返回结果
            if (params.isCollect) {
                _viewEvents.send(DetailsViewEvent.CollectEvent(message = app.getString(R.string.menu_collect_completed)))
                return@launch
            }

            flow {
                val response = meService.requestCollectAsync(params.id)
                if (response.errorCode == ApiConstants.REQUEST_OK) {
                    emit(true)
                } else {
                    throw RuntimeException(response.errorMsg)
                }
            }.onStart {
                _viewStates.update { it.copy(isLoading = true) }
            }.catch { error ->
                _viewEvents.send(DetailsViewEvent.CollectEvent(message = error.message))
            }.onCompletion {
                _viewStates.update { it.copy(isLoading = false) }
            }.lowestTime().collect { data ->
                params.isCollect = data
                _viewEvents.send(DetailsViewEvent.CollectEvent(message = app.getString(R.string.menu_collect_complete)))
            }
        }
    }

    private class DetailsParams(savedStateHandle: SavedStateHandle) {
        var title = savedStateHandle.get<String>(ARG_PARAMS_TITLE) ?: ""
        val url = savedStateHandle.get<String>(ARG_PARAMS_URL) ?: ""
        val id = savedStateHandle.get<String>(ARG_PARAMS_ID)?.toLongOrNull() ?: 0L
        var isCollect =
            savedStateHandle.get<String>(ARG_PARAMS_COLLECT)?.toBooleanStrictOrNull() ?: false
    }
}

data class DetailsViewState(
    val isLoading: Boolean = false,
    val title: String = "",
    val actionEnable: Boolean = false
)

sealed class DetailsViewEvent {
    data class ShareEvent(val shareText: String) : DetailsViewEvent()
    data class CollectEvent(val message: String?) : DetailsViewEvent()
}

sealed class DetailsViewAction {
    object RequestShareDetails : DetailsViewAction()
    object UpdateCollectStatus : DetailsViewAction()
}