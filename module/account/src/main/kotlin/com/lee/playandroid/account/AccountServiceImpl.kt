package com.lee.playandroid.account

import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.google.auto.service.AutoService
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.tools.PreferencesTools
import com.lee.library.viewstate.UiState
import com.lee.playandroid.account.constants.Constants
import com.lee.playandroid.account.viewmodel.AccountViewModel
import com.lee.playandroid.library.common.entity.AccountData
import com.lee.playandroid.library.common.entity.AccountViewAction
import com.lee.playandroid.library.common.entity.AccountViewEvent
import com.lee.playandroid.library.common.entity.AccountViewState
import com.lee.playandroid.library.service.AccountService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 *
 * @author jv.lee
 * @date 2021/11/25
 */
@AutoService(AccountService::class)
class AccountServiceImpl : AccountService {

    override fun getAccountViewStates(activity: FragmentActivity): StateFlow<AccountViewState> {
        return activity.viewModels<AccountViewModel>().value.viewStates
    }

    override fun getAccountViewEvents(activity: FragmentActivity): Flow<AccountViewEvent> {
        return activity.viewModels<AccountViewModel>().value.viewEvents
    }

    override suspend fun requestAccountInfo(activity: FragmentActivity) {
        activity.viewModels<AccountViewModel>().value.dispatch(AccountViewAction.RequestAccountData)
    }

    override suspend fun requestLogout(activity: FragmentActivity) {
        activity.viewModels<AccountViewModel>().value.dispatch(AccountViewAction.RequestLogout)
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