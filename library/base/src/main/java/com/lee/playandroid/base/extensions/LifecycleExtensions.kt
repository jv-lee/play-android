/*
 * 生命周期扩展函数帮助类
 * @author jv.lee
 * @date 2021/8/26
 */
package com.lee.playandroid.base.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

inline fun Lifecycle.create(crossinline call: () -> Unit) {
    addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_CREATE) {
                removeObserver(this)
                call()
            }
        }
    })
}

inline fun Lifecycle.destroy(crossinline call: () -> Unit) {
    addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                removeObserver(this)
                call()
            }
        }
    })
}