package com.lee.playandroid.square.viewmodel

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.stateFlow
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/12/21
 * @description
 */
class CreateShareViewModel : CoroutineViewModel() {

    private val _sendLive = MutableLiveData<UiState>()
    val sendLive: LiveData<UiState> = _sendLive

    fun requestSendShare(title: String, content: String) {
        launchIO {
            stateFlow {
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                    "success"
                } else {
                    throw Throwable("title || content is empty.")
                }
            }.collect {
                _sendLive.postValue(it)
            }
        }
    }

}