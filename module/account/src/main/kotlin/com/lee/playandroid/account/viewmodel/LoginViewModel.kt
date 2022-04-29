package com.lee.playandroid.account.viewmodel

import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.library.tools.PreferencesTools
import com.lee.playandroid.account.constants.Constants.SP_KEY_SAVE_INPUT_USERNAME
import com.lee.playandroid.account.model.api.ApiService
import com.lee.playandroid.library.common.entity.AccountData
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/3/23
 * @description
 */
class LoginViewModel : ViewModel() {

    private val api = createApi<ApiService>()

    private val _viewStates = MutableStateFlow(LoginViewState())
    val viewStates: StateFlow<LoginViewState> = _viewStates

    private val _viewEvents = Channel<LoginViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    init {
        restoreInputUsername()
    }

    fun dispatch(action: LoginViewAction) {
        when (action) {
            is LoginViewAction.ChangeUsername -> {
                changeUsername(action.username)
            }
            is LoginViewAction.ChangePassword -> {
                changePassword(action.password)
            }
            is LoginViewAction.RequestLogin -> {
                requestLogin()
            }
        }
    }

    private fun restoreInputUsername() {
        viewModelScope.launch {
            flow {
                val username = PreferencesTools.get<String>(SP_KEY_SAVE_INPUT_USERNAME)
                emit(username)
            }.collect { data ->
                _viewStates.update { it.copy(username = data) }
                changeLoginEnable()
            }
        }
    }

    private fun changeUsername(username: String) {
        _viewStates.update { it.copy(username = username) }
        changeLoginEnable()
    }

    private fun changePassword(password: String) {
        _viewStates.update { it.copy(password = password) }
        changeLoginEnable()
    }

    private fun changeLoginEnable() {
        _viewStates.update {
            it.copy(isLoginEnable = it.username.isNotEmpty() && it.password.isNotEmpty())
        }
    }

    private fun requestLogin() {
        viewModelScope.launch {
            flow {
                kotlinx.coroutines.delay(500)

                // 校验输入格式
                if (TextUtils.isEmpty(viewStates.value.username) || TextUtils.isEmpty(viewStates.value.password)) {
                    throw IllegalArgumentException("username || password is empty.")
                }

                api.postLoginAsync(viewStates.value.username, viewStates.value.password).checkData()
                val accountResponse = api.getAccountInfoAsync().checkData()
                emit(accountResponse)
            }.onStart {
                _viewStates.update { it.copy(isLoading = true) }
            }.catch { error ->
                _viewStates.update { it.copy(isLoading = false) }
                _viewEvents.send(LoginViewEvent.LoginFailed(error))
            }.collect { data ->
                // 缓存用户明输入信息下次复用
                PreferencesTools.put(SP_KEY_SAVE_INPUT_USERNAME, viewStates.value.username)
                _viewStates.update { it.copy(isLoading = false) }
                _viewEvents.send(LoginViewEvent.LoginSuccess(data))
            }
        }
    }
}

data class LoginViewState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoginEnable: Boolean = false,
)

sealed class LoginViewEvent {
    data class LoginSuccess(val accountData: AccountData) : LoginViewEvent()
    data class LoginFailed(val error: Throwable) : LoginViewEvent()
}

sealed class LoginViewAction {
    data class ChangeUsername(val username: String) : LoginViewAction()
    data class ChangePassword(val password: String) : LoginViewAction()
    object RequestLogin : LoginViewAction()
}