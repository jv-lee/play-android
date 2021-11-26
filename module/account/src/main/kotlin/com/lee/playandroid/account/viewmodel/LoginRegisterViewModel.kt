package com.lee.playandroid.account.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.library.mvvm.ui.UiState
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

    private val _accountLive = MutableLiveData<UiState>()
    val accountLive: LiveData<UiState> = _accountLive

    fun requestLogin(userName: String, password: String) {
        launchIO {
            stateFlow {
                repository.api.requestLogin(userName, password).checkData()
                repository.api.getAccountInfo().checkData()
            }.collect {
                _accountLive.postValue(it)
            }
        }
    }

    fun requestRegister(userName: String, password: String, rePassword: String) {
        launchIO {
            stateFlow {
                repository.api.requestRegister(userName, password, rePassword).checkData()
                repository.api.getAccountInfo().checkData()
            }.collect {
                _accountLive.postValue(it)
            }
        }
    }

}