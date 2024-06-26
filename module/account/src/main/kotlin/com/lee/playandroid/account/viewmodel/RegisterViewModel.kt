package com.lee.playandroid.account.viewmodel

import androidx.lifecycle.viewModelScope
import com.lee.playandroid.account.constants.Constants
import com.lee.playandroid.account.model.api.ApiService
import com.lee.playandroid.base.tools.PreferencesTools
import com.lee.playandroid.base.viewmodel.BaseMVIViewModel
import com.lee.playandroid.base.viewmodel.IViewEvent
import com.lee.playandroid.base.viewmodel.IViewIntent
import com.lee.playandroid.base.viewmodel.IViewState
import com.lee.playandroid.common.entity.AccountViewIntent
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 注册功能viewModel
 * @author jv.lee
 * @date 2022/3/23
 */
class RegisterViewModel :
    BaseMVIViewModel<RegisterViewState, RegisterViewEvent, RegisterViewIntent>() {

    private val api = createApi<ApiService>()

    override fun initViewState() = RegisterViewState()

    override fun dispatch(intent: RegisterViewIntent) {
        when (intent) {
            is RegisterViewIntent.ChangeUsername -> {
                changeUsername(intent.username)
            }

            is RegisterViewIntent.ChangePassword -> {
                changePassword(intent.password)
            }

            is RegisterViewIntent.ChangeRePassword -> {
                changeRePassword(intent.rePassword)
            }

            is RegisterViewIntent.RequestRegister -> {
                requestRegister()
            }

            is RegisterViewIntent.HideKeyboard -> {
                hideKeyboard()
            }

            is RegisterViewIntent.NavigationLogin -> {
                navigationLogin()
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
            it.copy(
                isRegisterEnable = it.username.isNotEmpty() &&
                        it.password.isNotEmpty() &&
                        it.rePassword.isNotEmpty()
            )
        }
    }

    private fun hideKeyboard() {
        _viewStates.update {
            it.copy(hideKeyboard = true)
        }
    }

    /**
     * 发起注册处理
     */
    private fun requestRegister() {
        viewModelScope.launch {
            flow {
                // 延时给予loading动画执行时间
                delay(500)

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
                _viewStates.update { it.copy(isLoading = true, hideKeyboard = true) }
            }.catch { error ->
                _viewStates.update { it.copy(isLoading = false, hideKeyboard = false) }
                _viewEvents.send(RegisterViewEvent.RegisterFailed(error))
            }.collect { data ->
                // 缓存用户名下次输入直接设置
                PreferencesTools.put(Constants.SP_KEY_SAVE_INPUT_USERNAME, data.userInfo.username)
                // 更新ui状态
                _viewStates.update { it.copy(isLoading = false, hideKeyboard = false) }
                // 发送注册成功事件携带账户ui状态
                val status = AccountViewIntent.UpdateAccountStatus(data, true)
                _viewEvents.send(RegisterViewEvent.RegisterSuccess(status))
            }
        }
    }

    /**
     * 回退至登陆页
     * 判断当前软键盘是否弹起，优先关闭软键盘
     */
    private fun navigationLogin() {
        viewModelScope.launch {
            _viewEvents.send(RegisterViewEvent.NavigationLoginEvent)
        }
    }
}

data class RegisterViewState(
    val username: String = "",
    val password: String = "",
    val rePassword: String = "",
    val isLoading: Boolean = false,
    val isRegisterEnable: Boolean = false,
    val hideKeyboard: Boolean = false
) : IViewState

sealed class RegisterViewEvent : IViewEvent {
    data class RegisterSuccess(val status: AccountViewIntent.UpdateAccountStatus) :
        RegisterViewEvent()

    data class RegisterFailed(val error: Throwable) : RegisterViewEvent()
    object NavigationLoginEvent : RegisterViewEvent()
}

sealed class RegisterViewIntent : IViewIntent {
    data class ChangeUsername(val username: String) : RegisterViewIntent()
    data class ChangePassword(val password: String) : RegisterViewIntent()
    data class ChangeRePassword(val rePassword: String) : RegisterViewIntent()
    object HideKeyboard : RegisterViewIntent()
    object RequestRegister : RegisterViewIntent()
    object NavigationLogin : RegisterViewIntent()
}