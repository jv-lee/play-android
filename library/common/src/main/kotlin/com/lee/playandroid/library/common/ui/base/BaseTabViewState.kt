package com.lee.playandroid.library.common.ui.base

import com.lee.playandroid.library.common.entity.Tab

/**
 * @author jv.lee
 * @date 2022/4/28
 * @description
 */

data class BaseTabViewState(val tabList: List<Tab> = emptyList(), val loading: Boolean = false)

sealed class BaseTabViewEvent {
    data class RequestFailed(val error: Throwable) : BaseTabViewEvent()
}

sealed class BaseTabViewAction {
    object RequestData : BaseTabViewAction()
}