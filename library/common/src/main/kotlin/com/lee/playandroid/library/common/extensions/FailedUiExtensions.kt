package com.lee.playandroid.library.common.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.lee.library.extensions.toast

/**
 * @author jv.lee
 * @date 2021/12/9
 * @description
 */

fun FragmentActivity.actionFailed(throwable: Throwable) {
    if (throwable.message != "Canceled") {
        toast(throwable.message)
    }
}

fun Fragment.actionFailed(throwable: Throwable) {
    if (throwable.message != "Canceled") {
        toast(throwable.message)
    }
}