package com.lee.playandroid.account.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.clearCache
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.stateCacheFlow
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.library.tools.PreferencesTools
import com.lee.library.utils.LogUtil
import com.lee.playandroid.account.constants.Constants
import com.lee.playandroid.account.model.repository.ApiRepository
import com.lee.playandroid.library.common.BuildConfig
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

    private val _accountLive = MutableLiveData<UiState>()
    val accountLive: LiveData<UiState> = _accountLive

    suspend fun requestAccountInfo() {
        stateCacheFlow({
            repository.api.getAccountInfoAsync().checkData().apply {
                PreferencesTools.put(Constants.SP_KEY_IS_LOGIN, true)
                cacheManager.putCache(Constants.CACHE_KEY_ACCOUNT_DATA, this)
            }
        }, {
            cacheManager.getCache<AccountData>(Constants.CACHE_KEY_ACCOUNT_DATA)
        }, {}).collect {
            _accountLive.postValue(it)
        }
    }

    fun requestLogout(
        showLoading: () -> Unit = {},
        hideLoading: () -> Unit = {},
        failedCall: (String) -> Unit = {}
    ) {
        launchMain {
            showLoading()
            val response = withIO { repository.api.getLogoutAsync() }
            if (response.errorCode == 0) {
                LogUtil.i("requestLogout:登出")
                PreferencesTools.put(Constants.SP_KEY_IS_LOGIN, false)
                PreferencesTools.put(BuildConfig.BASE_URI, "")
                cacheManager.clearCache(Constants.CACHE_KEY_ACCOUNT_DATA)
                _accountLive.postValue(UiState.Default)
            } else {
                failedCall(response.errorMsg)
            }
            hideLoading()
        }
    }

    fun updateAccountInfo(accountData: AccountData) {
        PreferencesTools.put(Constants.SP_KEY_IS_LOGIN, true)
        _accountLive.postValue(UiState.Success(accountData))
    }

    fun getAccountInfo(): AccountData? {
        return CacheManager.getDefault().getCache(Constants.CACHE_KEY_ACCOUNT_DATA)
    }

    init {
        LogUtil.i("initAccountViewModel")
    }

}