package com.lee.playandroid.account.viewmodel

import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.account.constants.Constants.SP_KEY_SAVE_INPUT_USERNAME
import com.lee.playandroid.account.model.api.ApiService
import com.lee.playandroid.base.tools.PreferencesTools
import com.lee.playandroid.base.viewmodel.BaseMVIViewModel
import com.lee.playandroid.base.viewmodel.IViewEvent
import com.lee.playandroid.base.viewmodel.IViewIntent
import com.lee.playandroid.base.viewmodel.IViewState
import com.lee.playandroid.common.entity.AccountViewIntent
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 登陆功能viewModel
 * @author jv.lee
 * @date 2022/3/23
 */
class LoginViewModel : BaseMVIViewModel<LoginViewState, LoginViewEvent, LoginViewIntent>() {

    private val api = createApi<ApiService>()

    init {
        restoreInputUsername()
    }

    override fun initViewState() = LoginViewState()

    override fun dispatch(intent: LoginViewIntent) {
        when (intent) {
            is LoginViewIntent.ChangeUsername -> {
                changeUsername(intent.username)
            }
            is LoginViewIntent.ChangePassword -> {
                changePassword(intent.password)
            }
            is LoginViewIntent.RequestLogin -> {
                requestLogin()
            }
            is LoginViewIntent.HideKeyboard -> {
                hideKeyboard()
            }
            is LoginViewIntent.NavigationRegister -> {
                navigationRegister()
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

    private fun hideKeyboard() {
        _viewStates.update {
            it.copy(hideKeyboard = true)
        }
    }

    /**
     * 发起登陆处理
     */
    private fun requestLogin() {
        viewModelScope.launch {
            flow {
                // 延时给予loading动画执行时间
                delay(500)

                // 校验输入格式
                if (TextUtils.isEmpty(viewStates.value.username) ||
                    TextUtils.isEmpty(viewStates.value.password)
                ) {
                    throw IllegalArgumentException("username || password is empty.")
                }

                api.postLoginAsync(viewStates.value.username, viewStates.value.password).checkData()
                val accountResponse = api.getAccountInfoAsync().checkData()
                emit(accountResponse)
            }.onStart {
                _viewStates.update { it.copy(isLoading = true, hideKeyboard = true) }
            }.catch { error ->
                _viewStates.update { it.copy(isLoading = false, hideKeyboard = false) }
                _viewEvents.send(LoginViewEvent.LoginFailed(error))
            }.collect { data ->
                // 缓存用户明输入信息下次复用
                PreferencesTools.put(SP_KEY_SAVE_INPUT_USERNAME, viewStates.value.username)
                // 更新ui状态
                _viewStates.update { it.copy(isLoading = false, hideKeyboard = false) }
                // 发送登陆成功事件携带账户ui状态
                val status = AccountViewIntent.UpdateAccountStatus(data, true)
                _viewEvents.send(LoginViewEvent.LoginSuccess(status))
            }
        }
    }

    /**
     * 跳转至注册页
     * 判断当前软键盘是否弹起，优先关闭软键盘
     */
    private fun navigationRegister() {
        viewModelScope.launch {
            _viewEvents.send(LoginViewEvent.NavigationRegisterEvent)
        }
    }
}

data class LoginViewState(
    // 设置登陆过的账户名
    val username: String = PreferencesTools.get(SP_KEY_SAVE_INPUT_USERNAME),
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoginEnable: Boolean = false,
    val hideKeyboard: Boolean = false
) : IViewState

sealed class LoginViewEvent : IViewEvent {
    data class LoginSuccess(val status: AccountViewIntent.UpdateAccountStatus) : LoginViewEvent()
    data class LoginFailed(val error: Throwable) : LoginViewEvent()
    object NavigationRegisterEvent : LoginViewEvent()
}

sealed class LoginViewIntent : IViewIntent {
    data class ChangeUsername(val username: String) : LoginViewIntent()
    data class ChangePassword(val password: String) : LoginViewIntent()
    object HideKeyboard : LoginViewIntent()
    object RequestLogin : LoginViewIntent()
    object NavigationRegister : LoginViewIntent()
}