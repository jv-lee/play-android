/*
 * AgentWebView扩展函数
 * @author jv.lee
 * @date 2021/11/18
 */
package com.lee.playandroid.common.extensions

import android.app.Activity
import android.content.res.Configuration
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.just.agentweb.AgentWeb
import com.lee.playandroid.common.BuildConfig

/**
 * agentWebView 预加载方法
 */
fun Activity.agentWebPreload() {
    AgentWeb.with(this)
        .setAgentWebParent(
            FrameLayout(this),
            LinearLayout.LayoutParams(1, 1)
        )
        .useDefaultIndicator()
        .createAgentWeb()
        .go(BuildConfig.BASE_URI)
}

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

/**
 * webView适配深色模式
 */
@Suppress("DEPRECATION")
fun AgentWeb.supportDarkMode(): AgentWeb {
    val settings = this.agentWebSettings.webSettings
    val resources = webCreator.webView.context.resources

    if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_ON)
            }
            Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_OFF)
            }
        }
    }
    return this
}