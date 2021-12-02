package com.lee.playandroid.library.common.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.just.agentweb.AgentWeb

/**
 * @author jv.lee
 * @date 2021/11/18
 * @description
 */
fun AgentWeb.bindLifecycle(lifecycle: Lifecycle): AgentWeb {
    lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_PAUSE -> webLifeCycle.onPause()
                Lifecycle.Event.ON_RESUME -> webLifeCycle.onResume()
                Lifecycle.Event.ON_DESTROY -> {
                    webLifeCycle.onDestroy()
                    lifecycle.removeObserver(this)
                }
                else -> {
                }
            }
        }
    })
    return this
}