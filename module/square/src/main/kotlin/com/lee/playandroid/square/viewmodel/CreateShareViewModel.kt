package com.lee.playandroid.square.viewmodel

import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.base.ApplicationExtensions.app
import com.lee.playandroid.base.viewmodel.BaseMVIViewModel
import com.lee.playandroid.base.viewmodel.IViewEvent
import com.lee.playandroid.base.viewmodel.IViewIntent
import com.lee.playandroid.base.viewmodel.IViewState
import com.lee.playandroid.common.constants.ApiConstants
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.square.R
import com.lee.playandroid.square.model.api.ApiService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 创建分享内容viewModel
 * @author jv.lee
 * @date 2021/12/21
 */
class CreateShareViewModel :
    BaseMVIViewModel<CreateShareViewState, CreateShareViewEvent, CreateShareViewIntent>() {

    private val api = createApi<ApiService>()

    override fun initViewState() = CreateShareViewState()

    override fun dispatch(intent: CreateShareViewIntent) {
        when (intent) {
            is CreateShareViewIntent.RequestSend -> {
                requestSendShare(intent.title, intent.content)
            }
        }
    }

    private fun requestSendShare(title: String, content: String) {
        viewModelScope.launch {
            // 校验输入格式
            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                _viewEvents.send(
                    CreateShareViewEvent.SendFailed(Throwable("title || content is empty."))
                )
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

data class CreateShareViewState(val loading: Boolean = false) : IViewState

sealed class CreateShareViewEvent : IViewEvent {
    data class SendSuccess(val message: String) : CreateShareViewEvent()
    data class SendFailed(val error: Throwable) : CreateShareViewEvent()
}

sealed class CreateShareViewIntent : IViewIntent {
    data class RequestSend(val title: String, val content: String) : CreateShareViewIntent()
}