/*
 * 项目通用tab+list页面ViewModel状态实体
 * @author jv.lee
 * @date 2022/4/28
 */
package com.lee.playandroid.common.ui.base

import com.lee.playandroid.base.viewmodel.IViewEvent
import com.lee.playandroid.base.viewmodel.IViewIntent
import com.lee.playandroid.base.viewmodel.IViewState
import com.lee.playandroid.common.entity.Tab

data class BaseTabViewState(val tabList: List<Tab> = emptyList(), val loading: Boolean = true) :
    IViewState

sealed class BaseTabViewEvent : IViewEvent {
    data class RequestFailed(val error: Throwable) : BaseTabViewEvent()
}

sealed class BaseTabViewIntent : IViewIntent {
    object RequestData : BaseTabViewIntent()
}