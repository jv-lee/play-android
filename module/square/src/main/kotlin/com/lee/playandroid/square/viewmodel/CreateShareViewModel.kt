package com.lee.playandroid.square.viewmodel

import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.library.base.ApplicationExtensions.app
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.square.R
import com.lee.playandroid.square.model.api.ApiService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 *
 * @author jv.lee
 * @date 2021/12/21
 */
class CreateShareViewModel : ViewModel() {

    private val api = createApi<ApiService>()

    private val _viewStates = MutableStateFlow(CreateShareViewState())
    val viewStates: StateFlow<CreateShareViewState> = _viewStates

    private val _viewEvents = Channel<CreateShareViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: CreateShareViewAction) {
        when (action) {
            is CreateShareViewAction.RequestSend -> {
                requestSendShare(action.title, action.content)
            }
        }
    }

    private fun requestSendShare(title: String, content: String) {
        viewModelScope.launch {
            // 校验输入格式
            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                _viewEvents.send(CreateShareViewEvent.SendFailed(Throwable("title || content is empty.")))
            } else {
                flow {
                    val response = api.postShareDataSync(title, content)
                    if (response.errorCode == ApiConstants.REQUEST_OK) {
                        emit(app.getString(R.string.share_success))
                    } else {
                        throw RuntimeException(response.errorMsg)
                    }
                }.onStart {
                    _viewStates.update { it.copy(loading = true) }
                }.catch { error ->
                    _viewStates.update { it.copy(loading = false) }
                    _viewEvents.send(CreateShareViewEvent.SendFailed(error))
                }.collect { data ->
                    _viewStates.update { it.copy(loading = false) }
                    _viewEvents.send(CreateShareViewEvent.SendSuccess(message = data))
                }
            }
        }
    }

}

data class CreateShareViewState(val loading: Boolean = false)

sealed class CreateShareViewEvent {
    data class SendSuccess(val message: String) : CreateShareViewEvent()
    data class SendFailed(val error: Throwable) : CreateShareViewEvent()
}

sealed class CreateShareViewAction {
    data class RequestSend(val title: String, val content: String) : CreateShareViewAction()
}