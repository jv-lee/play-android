package com.lee.playandroid.account.viewmodel

import com.lee.library.mvvm.ui.UiStateLiveData
import com.lee.library.mvvm.ui.UiStateMutableLiveData
import com.lee.library.mvvm.ui.stateFlow
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.account.model.repository.ApiRepository
import com.lee.playandroid.library.common.extensions.checkData
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description 登陆注册ViewModel
 */
class LoginRegisterViewModel : CoroutineViewModel() {

    private val repository = ApiRepository()

    private val _accountLive = UiStateMutableLiveData()
    val accountLive: UiStateLiveData = _accountLive

    fun requestLogin(userName: String, password: String) {
        launchIO {
            stateFlow {
                repository.api.postLoginAsync(userName, password).checkData()
                repository.api.getAccountInfoAsync().checkData()
            }.collect {
                _accountLive.postValue(it)
            }
        }
    }

    fun requestRegister(userName: String, password: String, rePassword: String) {
        launchIO {
            stateFlow {
                repository.api.postRegisterAsync(userName, password, rePassword).checkData()
                repository.api.getAccountInfoAsync().checkData()
            }.collect {
                _accountLive.postValue(it)
            }
        }
    }

}