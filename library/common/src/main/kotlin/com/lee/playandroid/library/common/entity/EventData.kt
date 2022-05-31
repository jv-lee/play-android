package com.lee.playandroid.library.common.entity

import androidx.annotation.Keep

/**
 * liveDataBus 事件通知实体
 * @author jv.lee
 * @date 2021/11/17
 */

@Keep
data class NavigationSelectEvent(val title: String)

@Keep
class LoginEvent