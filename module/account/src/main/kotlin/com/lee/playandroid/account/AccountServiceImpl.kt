package com.lee.playandroid.account

import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import com.google.auto.service.AutoService
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.mvvm.ui.UiState
import com.lee.library.tools.PreferencesTools
import com.lee.playandroid.account.constants.Constants
import com.lee.playandroid.account.viewmodel.AccountViewModel
import com.lee.playandroid.library.common.entity.AccountData
import com.lee.playandroid.library.service.AccountService

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description
 */
@AutoService(AccountService::class)
class AccountServiceImpl : AccountService {

    override fun getAccountLive(activity: FragmentActivity): LiveData<UiState> {
        return activity.viewModels<AccountViewModel>().value.accountLive
    }

    override fun getAccountInfo(activity: FragmentActivity): AccountData? {
        return activity.viewModels<AccountViewModel>().value.getAccountInfo()
    }

    override suspend fun requestAccountInfo(activity: FragmentActivity) {
        activity.viewModels<AccountViewModel>().value.requestAccountInfo()
    }

    override fun requestLogout(
        activity: FragmentActivity,
        showLoading: () -> Unit,
        hideLoading: () -> Unit,
        failedCall: (String) -> Unit
    ) {
        activity.viewModels<AccountViewModel>().value.requestLogout(
            showLoading,
            hideLoading,
            failedCall
        )
    }

    override fun getAccountInfo(): AccountData? {
        return CacheManager.getDefault().getCache(Constants.CACHE_KEY_ACCOUNT_DATA)
    }

    override fun getUserId(): Long {
        return getAccountInfo()?.run { userInfo.id } ?: kotlin.run { 0 }
    }

    override fun isLogin(): Boolean {
        return PreferencesTools.get(Constants.SP_KEY_IS_LOGIN)
    }

}