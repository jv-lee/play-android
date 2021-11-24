package com.lee.playandroid.library.common.entity

import androidx.annotation.Keep

/**
 * @author jv.lee
 * @data 2021/11/17
 * @description
 */
@Keep
data class NavigationSelectEvent(val title: String) {
    companion object {
        const val key = "NavigationSelectEvent"
    }
}