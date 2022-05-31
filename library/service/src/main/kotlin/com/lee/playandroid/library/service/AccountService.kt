package com.lee.playandroid.library.service

import androidx.fragment.app.FragmentActivity
import com.lee.playandroid.library.common.entity.AccountData
import com.lee.playandroid.library.common.entity.AccountViewEvent
import com.lee.playandroid.library.common.entity.AccountViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 *
 * @author jv.lee
 * @date 2021/11/25
 */
interface AccountService {

    fun getAccountViewStates(activity: FragmentActivity): StateFlow<AccountViewState>

    fun getAccountViewEvents(activity: FragmentActivity): Flow<AccountViewEvent>

    suspend fun requestAccountInfo(activity: FragmentActivity)

    suspend fun requestLogout(activity: FragmentActivity)

    fun getAccountInfo(): AccountData?

    fun getUserId(): Long

    fun isLogin(): Boolean

}