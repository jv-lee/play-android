package com.lee.playandroid.library.common.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.just.agentweb.AgentWeb

/**
 * @author jv.lee
 * @data 2021/11/18
 * @description
 */
fun AgentWeb.bindLifecycle(lifecycle: Lifecycle): AgentWeb {
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            webLifeCycle.onPause()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            webLifeCycle.onResume()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroyView() {
            webLifeCycle.onDestroy()
            lifecycle.removeObserver(this)
        }
    })
    return this
}