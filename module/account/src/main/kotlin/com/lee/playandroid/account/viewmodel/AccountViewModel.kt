package com.lee.playandroid.account.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.library.base.ApplicationExtensions.app
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.clearCache
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.tools.PreferencesTools
import com.lee.playandroid.account.R
import com.lee.playandroid.account.constants.Constants.CACHE_KEY_ACCOUNT_DATA
import com.lee.playandroid.account.constants.Constants.SP_KEY_IS_LOGIN
import com.lee.playandroid.account.model.api.ApiService
import com.lee.playandroid.library.common.BuildConfig
import com.lee.playandroid.library.common.constants.ApiConstants.REQUEST_TOKEN_ERROR_MESSAGE
import com.lee.playandroid.library.common.entity.AccountData
import com.lee.playandroid.library.common.entity.AccountViewAction
import com.lee.playandroid.library.common.entity.AccountViewEvent
import com.lee.playandroid.library.common.entity.AccountViewState
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
class AccountViewModel : ViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()

    private val _viewStates = MutableStateFlow(AccountViewState())
    val viewStates: StateFlow<AccountViewState> = _viewStates

    private val _viewEvents = Channel<AccountViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    suspend fun dispatch(action: AccountViewAction) {
        when (action) {
            is AccountViewAction.RequestAccountData -> {
                requestAccountData()
            }
            is AccountViewAction.RequestLogout -> {
                requestLogout()
            }
            is AccountViewAction.UpdateAccountStatus -> {
                updateAccountStatus(action.accountData, action.isLogin)
            }

        }
    }

    private suspend fun requestAccountData() {
        flow {
            emit(api.getAccountInfoAsync().checkData())
        }.onStart {
            cacheManager.getCache<AccountData>(CACHE_KEY_ACCOUNT_DATA)?.let {
                emit(it)
            }
        }.catch { error ->
            // 登陆token失效
            if (error.message == REQUEST_TOKEN_ERROR_MESSAGE) {
                updateAccountStatus(null, false)
            }
        }.collect {
            updateAccountStatus(it, true)
        }
    }

    /**
     * 请求登出
     */
    private fun requestLogout() {
        viewModelScope.launch {
            flow {
                kotlinx.coroutines.delay(500)
                emit(api.getLogoutAsync().checkData())
            }.onStart {
                _viewStates.update { it.copy(isLoading = true) }
            }.catch { error ->
                _viewStates.update { it.copy(isLoading = false) }
                _viewEvents.send(AccountViewEvent.LogoutFailed(error.message))
            }.collect {
                _viewStates.update { it.copy(isLoading = false) }
                updateAccountStatus(null, false)
                _viewEvents.send(AccountViewEvent.LogoutSuccess(app.getString(R.string.account_logout_success)))
            }
        }
    }

    /**
     * @param accountData 账户数据
     * @param isLogin 登陆结果
     */
    private fun updateAccountStatus(accountData: AccountData?, isLogin: Boolean) {
        _viewStates.update {
            if (isLogin) {
                cacheManager.putCache(CACHE_KEY_ACCOUNT_DATA, accountData)
                PreferencesTools.put(SP_KEY_IS_LOGIN, true)
                it.copy(accountData = accountData, isLogin = isLogin)
            } else {
                cacheManager.clearCache(CACHE_KEY_ACCOUNT_DATA)
                PreferencesTools.put(SP_KEY_IS_LOGIN, false)
                PreferencesTools.put(BuildConfig.BASE_URI, "")
                it.copy(accountData = accountData, isLogin = isLogin)
            }
        }
    }

}