package com.lee.playandroid.library.service

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import com.lee.library.mvvm.ui.UiState

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description
 */
interface AccountService {

    fun getAccountLive(activity: FragmentActivity): LiveData<UiState>

    suspend fun requestAccountInfo(activity: FragmentActivity)

    fun requestLogout(
        activity: FragmentActivity,
        showLoading: () -> Unit = {},
        hideLoading: () -> Unit = {},
        failedCall: (String) -> Unit = {}
    )

    fun isLogin(): Boolean

}