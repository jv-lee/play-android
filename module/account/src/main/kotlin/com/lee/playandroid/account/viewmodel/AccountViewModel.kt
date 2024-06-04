package com.lee.playandroid.account.viewmodel

import androidx.lifecycle.viewModelScope
import com.lee.playandroid.account.R
import com.lee.playandroid.account.constants.Constants.CACHE_KEY_ACCOUNT_DATA
import com.lee.playandroid.account.constants.Constants.SP_KEY_IS_LOGIN
import com.lee.playandroid.account.model.api.ApiService
import com.lee.playandroid.base.base.ApplicationExtensions.app
import com.lee.playandroid.base.cache.CacheManager
import com.lee.playandroid.base.extensions.clearCache
import com.lee.playandroid.base.extensions.getCache
import com.lee.playandroid.base.extensions.putCache
import com.lee.playandroid.base.tools.PreferencesTools
import com.lee.playandroid.base.viewmodel.BaseMVIViewModel
import com.lee.playandroid.common.constants.ApiConstants.REQUEST_TOKEN_ERROR_MESSAGE
import com.lee.playandroid.common.entity.AccountData
import com.lee.playandroid.common.entity.AccountViewEvent
import com.lee.playandroid.common.entity.AccountViewIntent
import com.lee.playandroid.common.entity.AccountViewState
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 账户操作ViewModel
 * @author jv.lee
 * @date 2022/3/23
 */
class AccountViewModel : BaseMVIViewModel<AccountViewState, AccountViewEvent, AccountViewIntent>() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()

    override fun initViewState() = AccountViewState()

    override fun dispatch(intent: AccountViewIntent) {
        when (intent) {
            is AccountViewIntent.RequestAccountData -> {
                requestAccountData()
            }

            is AccountViewIntent.RequestLogout -> {
                requestLogout()
            }

            is AccountViewIntent.UpdateAccountStatus -> {
                updateAccountStatus(intent.accountData, intent.isLogin)
            }

            is AccountViewIntent.ClearLoginState -> {
                updateAccountStatus(null, false)
            }
        }
    }

    private fun requestAccountData() {
        viewModelScope.launch {
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
                _viewEvents.send(
                    AccountViewEvent.LogoutSuccess(app.getString(R.string.account_logout_success))
                )
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
            } else {
                cacheManager.clearCache(CACHE_KEY_ACCOUNT_DATA)
                PreferencesTools.put(SP_KEY_IS_LOGIN, false)
            }
            it.copy(accountData = accountData, isLogin = isLogin)
        }
    }
}