package com.lee.playandroid.account.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.library.tools.PreferencesTools
import com.lee.playandroid.account.constants.Constants
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
class RegisterViewModel : ViewModel() {

    private val api = createApi<ApiService>()

    private val _viewStates = MutableStateFlow(RegisterViewState())
    val viewStates: StateFlow<RegisterViewState> = _viewStates

    private val _viewEvents = Channel<RegisterViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: RegisterViewAction) {
        when (action) {
            is RegisterViewAction.ChangeUsername -> {
                changeUsername(action.username)
            }
            is RegisterViewAction.ChangePassword -> {
                changePassword(action.password)
            }
            is RegisterViewAction.ChangeRePassword -> {
                changeRePassword(action.rePassword)
            }
            is RegisterViewAction.RequestRegister -> {
                requestRegister()
            }
        }
    }

    private fun changeUsername(username: String) {
        _viewStates.update { it.copy(username = username) }
        changeRegisterEnable()
    }

    private fun changePassword(password: String) {
        _viewStates.update { it.copy(password = password) }
        changeRegisterEnable()
    }

    private fun changeRePassword(rePassword: String) {
        _viewStates.update { it.copy(rePassword = rePassword) }
        changeRegisterEnable()
    }

    private fun changeRegisterEnable() {
        _viewStates.update {
            it.copy(isRegisterEnable = it.username.isNotEmpty() && it.password.isNotEmpty() && it.rePassword.isNotEmpty())
        }
    }

    private fun requestRegister() {
        viewModelScope.launch {
            flow {
                kotlinx.coroutines.delay(500)

                // 校验输入格式
                if (viewStates.value.username.isEmpty() ||
                    viewStates.value.password.isEmpty() ||
                    viewStates.value.rePassword.isEmpty()
                ) {
                    throw IllegalArgumentException("username || password || repassword is empty.")
                }

                api.postRegisterAsync(
                    viewStates.value.username,
                    viewStates.value.password,
                    viewStates.value.rePassword
                ).checkData()

                val accountResponse = api.getAccountInfoAsync().checkData()
                emit(accountResponse)
            }.onStart {
                _viewStates.update { it.copy(isLoading = true) }
            }.catch { error ->
                _viewStates.update { it.copy(isLoading = false) }
                _viewEvents.send(RegisterViewEvent.RegisterFailed(error))
            }.collect { data ->
                // 缓存用户名下次输入直接设置
                PreferencesTools.put(Constants.SP_KEY_SAVE_INPUT_USERNAME, data.userInfo.username)
                _viewStates.update { it.copy(isLoading = false) }
                _viewEvents.send(RegisterViewEvent.RegisterSuccess(data))
            }
        }
    }
}

data class RegisterViewState(
    val username: String = "",
    val password: String = "",
    val rePassword: String = "",
    val isLoading: Boolean = false,
    val isRegisterEnable: Boolean = false,
)

sealed class RegisterViewEvent {
    data class RegisterSuccess(val accountData: AccountData) : RegisterViewEvent()
    data class RegisterFailed(val error: Throwable) : RegisterViewEvent()
}

sealed class RegisterViewAction {
    data class ChangeUsername(val username: String) : RegisterViewAction()
    data class ChangePassword(val password: String) : RegisterViewAction()
    data class ChangeRePassword(val rePassword: String) : RegisterViewAction()
    object RequestRegister : RegisterViewAction()
}