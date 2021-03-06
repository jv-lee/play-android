/*
 * AgentWebView扩展函数
 * @author jv.lee
 * @date 2021/11/18
 */
package com.lee.playandroid.common.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.just.agentweb.AgentWeb

/**
 * AgentWebView 绑定生命周期控制生命状态
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