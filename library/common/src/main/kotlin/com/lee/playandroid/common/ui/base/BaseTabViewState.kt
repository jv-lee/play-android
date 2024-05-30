/*
 * 项目通用tab+list页面ViewModel状态实体
 * @author jv.lee
 * @date 2022/4/28
 */
package com.lee.playandroid.common.ui.base

import com.lee.playandroid.common.entity.Tab

data class BaseTabViewState(val tabList: List<Tab> = emptyList(), val loading: Boolean = true)

sealed class BaseTabViewEvent {
    data class RequestFailed(val error: Throwable) : BaseTabViewEvent()
}

sealed class BaseTabViewIntent {
    object RequestData : BaseTabViewIntent()
}