package com.lee.playandroid.account.viewmodel

import com.lee.library.cache.CacheManager
import com.lee.library.extensions.clearCache
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.UiStateLiveData
import com.lee.library.mvvm.ui.UiStateMutableLiveData
import com.lee.library.mvvm.ui.stateFlow
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.library.tools.PreferencesTools
import com.lee.library.utils.LogUtil
import com.lee.playandroid.account.constants.Constants
import com.lee.playandroid.account.model.repository.ApiRepository
import com.lee.playandroid.library.common.BuildConfig
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.common.entity.AccountData
import com.lee.playandroid.library.common.extensions.checkData
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description 账户信息ViewModel 作用于全局 - 基于MainActivity
 * 其他模块通过 ModuleService.find<AccountService>().getAccountLive(requireActivity()) 获取LiveData进行账户数据监听
 */
class AccountViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    private val _accountLive = UiStateMutableLiveData()
    val accountLive: UiStateLiveData = _accountLive

    /**
     * 请求账户数据
     */
    suspend fun requestAccountInfo() {
        stateFlow({
            repository.api.getAccountInfoAsync().checkData().apply {
                updateAccountStatus(this, true)
            }
        }, {
            cacheManager.getCache(Constants.CACHE_KEY_ACCOUNT_DATA)
        }, {}).collect {
            _accountLive.postValue(it)
        }
    }

    /**
     * 请求登出
     */
    fun requestLogout(
        showLoading: () -> Unit = {},
        hideLoading: () -> Unit = {},
        failedCall: (String) -> Unit = {}
    ) {
        launchMain {
            showLoading()
            val response = withIO { repository.api.getLogoutAsync() }
            if (response.errorCode == ApiConstants.REQUEST_OK) {
                updateAccountStatus(null, false)
                _accountLive.postValue(UiState.Default)
            } else {
                failedCall(response.errorMsg)
            }
            hideLoading()
        }
    }

    /**
     * 更新当前账户缓存信息
     */
    fun updateAccountInfo(accountData: AccountData) {
        updateAccountStatus(accountData, true)
        _accountLive.postValue(UiState.Success(accountData))
    }

    /**
     * 获取当前账户缓存信息
     */
    fun getAccountInfo(): AccountData? {
        return cacheManager.getCache(Constants.CACHE_KEY_ACCOUNT_DATA)
    }

    /**
     * @param accountData 账户数据
     * @param isLogin 登陆结果
     */
    private fun updateAccountStatus(accountData: AccountData?, isLogin: Boolean) {
        if (isLogin) {
            cacheManager.putCache(Constants.CACHE_KEY_ACCOUNT_DATA, accountData)
            PreferencesTools.put(Constants.SP_KEY_IS_LOGIN, true)
        } else {
            cacheManager.clearCache(Constants.CACHE_KEY_ACCOUNT_DATA)
            PreferencesTools.put(Constants.SP_KEY_IS_LOGIN, false)
            PreferencesTools.put(BuildConfig.BASE_URI, "")
        }
    }

    init {
        LogUtil.i("initAccountViewModel")
    }

}