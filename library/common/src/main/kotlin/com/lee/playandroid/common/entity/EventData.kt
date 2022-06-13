/*
 * liveDataBus 事件通知实体
 * @author jv.lee
 * @date 2021/11/17
 */
package com.lee.playandroid.common.entity

import androidx.annotation.Keep

/**
 * 主页导航tab选中事件
 * @param title 选中tab的title
 */
@Keep
data class NavigationSelectEvent(val title: String)

/**
 * 账号登陆事件
 */
@Keep
class LoginEvent