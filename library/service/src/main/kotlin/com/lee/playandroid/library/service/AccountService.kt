package com.lee.playandroid.library.service

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import com.lee.playandroid.library.common.entity.AccountData

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description
 */
interface AccountService {
    fun getAccountLive(activity: FragmentActivity): LiveData<AccountData>
    suspend fun requestAccountInfo(activity: FragmentActivity)
    fun loginUser(activity: FragmentActivity)
    fun isLogin(): Boolean
}