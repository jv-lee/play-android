package com.lee.playandroid.common.ui.base

import com.lee.playandroid.common.entity.Tab

/**
 *
 * @author jv.lee
 * @date 2022/4/28
 */

data class BaseTabViewState(val tabList: List<Tab> = emptyList(), val loading: Boolean = true)

sealed class BaseTabViewEvent {
    data class RequestFailed(val error: Throwable) : BaseTabViewEvent()
}

sealed class BaseTabViewAction {
    object RequestData : BaseTabViewAction()
}