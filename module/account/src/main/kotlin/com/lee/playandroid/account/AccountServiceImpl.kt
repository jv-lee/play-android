package com.lee.playandroid.account

import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import com.google.auto.service.AutoService
import com.lee.library.mvvm.ui.UiState
import com.lee.library.tools.PreferencesTools
import com.lee.playandroid.account.constants.Constants
import com.lee.playandroid.account.viewmodel.AccountViewModel
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

    override suspend fun requestAccountInfo(activity: FragmentActivity) {
        activity.viewModels<AccountViewModel>().value.requestAccountInfo()
    }

    override fun requestLogout(activity: FragmentActivity) {
        activity.viewModels<AccountViewModel>().value.requestLogout()
    }

    override fun isLogin(): Boolean {
        return PreferencesTools.get(Constants.KEY_IS_LOGIN, false)
    }


}