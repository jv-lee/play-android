package com.lee.playandroid.service

import androidx.fragment.app.FragmentActivity
import com.lee.playandroid.common.entity.AccountData
import com.lee.playandroid.common.entity.AccountViewEvent
import com.lee.playandroid.common.entity.AccountViewState
import com.lee.playandroid.service.core.IModuleService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * 账户模块对外提供功能服务类
 * @author jv.lee
 * @date 2021/11/25
 */
interface AccountService : IModuleService {

    /**
     * 获取账户viewState，其他模块渲染账户相关ui时根据该state控制
     * @param activity
     */
    fun getAccountViewStates(activity: FragmentActivity): StateFlow<AccountViewState>

    /**
     * 获取账户viewEvent，其他模块监听该事处理制账户事件
     */
    fun getAccountViewEvents(activity: FragmentActivity): Flow<AccountViewEvent>

    /**
     * 请求全局AccountViewModel 请求账户信息
     * @param activity
     */
    suspend fun requestAccountInfo(activity: FragmentActivity)

    /**
     * 请求全局AccountViewModel 请求登出
     * @param activity
     */
    suspend fun requestLogout(activity: FragmentActivity)

    /**
     * 获取当前登陆的账户信息
     */
    fun getAccountInfo(): AccountData?

    /**
     * 获取当前登陆用户id
     */
    fun getUserId(): Long

    /**
     * 获取当前是否已登陆
     */
    fun isLogin(): Boolean

}