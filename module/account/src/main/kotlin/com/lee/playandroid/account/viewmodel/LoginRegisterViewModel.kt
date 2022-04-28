package com.lee.playandroid.account.viewmodel

import com.lee.library.viewstate.UiStateLiveData
import com.lee.library.viewstate.UiStateMutableLiveData
import com.lee.library.viewstate.stateFlow
import com.lee.library.viewmodel.CoroutineViewModel
import com.lee.playandroid.account.model.api.ApiService
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description 登陆注册ViewModel
 */
class LoginRegisterViewModel : CoroutineViewModel() {

    private val api = createApi<ApiService>()

    private val _accountLive = UiStateMutableLiveData()
    val accountLive: UiStateLiveData = _accountLive

    fun requestLogin(userName: String, password: String) {
        launchIO {
            stateFlow {
                api.postLoginAsync(userName, password).checkData()
                api.getAccountInfoAsync().checkData()
            }.collect {
                _accountLive.postValue(it)
            }
        }
    }

    fun requestRegister(userName: String, password: String, rePassword: String) {
        launchIO {
            stateFlow {
                api.postRegisterAsync(userName, password, rePassword).checkData()
                api.getAccountInfoAsync().checkData()
            }.collect {
                _accountLive.postValue(it)
            }
        }
    }

}