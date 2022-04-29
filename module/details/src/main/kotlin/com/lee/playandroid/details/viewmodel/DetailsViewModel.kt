package com.lee.playandroid.details.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.lee.library.viewmodel.CoroutineViewModel
import com.lee.playandroid.details.R
import com.lee.playandroid.details.ui.DetailsFragment.Companion.ARG_PARAMS_COLLECT
import com.lee.playandroid.details.ui.DetailsFragment.Companion.ARG_PARAMS_ID
import com.lee.playandroid.details.ui.DetailsFragment.Companion.ARG_PARAMS_TITLE
import com.lee.playandroid.details.ui.DetailsFragment.Companion.ARG_PARAMS_URL
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.service.MeService
import com.lee.playandroid.library.service.hepler.ModuleService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2021/12/3
 * @description
 */
class DetailsViewModel(savedStateHandle: SavedStateHandle) : CoroutineViewModel() {

    private var title = savedStateHandle.get<String>(ARG_PARAMS_TITLE) ?: ""
    private val detailsUrl = savedStateHandle.get<String>(ARG_PARAMS_URL) ?: ""
    private val id = savedStateHandle.get<String>(ARG_PARAMS_ID)?.toLongOrNull() ?: 0L
    private var isCollect =
        savedStateHandle.get<String>(ARG_PARAMS_COLLECT)?.toBooleanStrictOrNull() ?: false

    private val meService = ModuleService.find<MeService>()

    private var _viewStates =
        MutableStateFlow(DetailsViewState(title = title, actionEnable = id != 0L))
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
            _viewEvents.send(DetailsViewEvent.ShareDetailsEvent("$title:$detailsUrl"))
        }
    }

    private fun requestCollect() {
        viewModelScope.launch {
            //已收藏直接返回结果
            if (isCollect) {
                _viewEvents.send(DetailsViewEvent.CollectEvent(messageRes = R.string.menu_collect_completed))
                return@launch
            }

            flow {
                val response = meService.requestCollectAsync(id)
                if (response.errorCode == ApiConstants.REQUEST_OK) {
                    emit(true)
                } else {
                    throw RuntimeException(response.errorMsg)
                }
            }.catch { error ->
                _viewEvents.send(DetailsViewEvent.CollectFailed(error = error))
            }.collect { data ->
                isCollect = data
                _viewEvents.send(DetailsViewEvent.CollectEvent(messageRes = R.string.menu_collect_complete))
            }
        }
    }

}

data class DetailsViewState(
    val title: String = "",
    val actionEnable: Boolean = false
)

sealed class DetailsViewEvent {
    data class ShareDetailsEvent(val shareContent: String) : DetailsViewEvent()
    data class CollectEvent(val messageRes: Int) : DetailsViewEvent()
    data class CollectFailed(val error: Throwable) : DetailsViewEvent()
}

sealed class DetailsViewAction {
    object RequestShareDetails : DetailsViewAction()
    object UpdateCollectStatus : DetailsViewAction()
}