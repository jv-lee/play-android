package com.lee.playandroid.square.viewmodel

import android.text.TextUtils
import com.lee.library.base.ApplicationExtensions.app
import com.lee.library.viewstate.UiStateLiveData
import com.lee.library.viewstate.UiStateMutableLiveData
import com.lee.library.viewstate.stateFlow
import com.lee.library.viewmodel.CoroutineViewModel
import com.lee.library.viewstate.UiState
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.square.R
import com.lee.playandroid.square.model.api.ApiService
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/12/21
 * @description
 */
class CreateShareViewModel : CoroutineViewModel() {

    private val api = createApi<ApiService>()

    private val _sendLive = UiStateMutableLiveData()
    val sendLive: UiStateLiveData = _sendLive

    fun requestSendShare(title: String, content: String) {
        // 校验输入格式
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            _sendLive.postValue(UiState.Error(Throwable("title || content is empty.")))
            return
        }
        launchIO {
            stateFlow {
                val response = api.postShareDataSync(title, content)
                if (response.errorCode == ApiConstants.REQUEST_OK) {
                    app.getString(R.string.share_success)
                } else {
                    throw RuntimeException(response.errorMsg)
                }
            }.collect {
                _sendLive.postValue(it)
            }
        }
    }

}