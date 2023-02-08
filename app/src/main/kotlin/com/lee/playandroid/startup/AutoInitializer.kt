package com.lee.playandroid.startup

import android.content.Context
import androidx.startup.Initializer
import com.lee.playandroid.BuildConfig
import com.lee.playandroid.base.adapter.manager.ViewLoadManager
import com.lee.playandroid.base.cache.CacheManager
import com.lee.playandroid.base.net.HttpManager
import com.lee.playandroid.base.tools.DarkModeTools
import com.lee.playandroid.base.tools.ScreenDensityTools
import com.lee.playandroid.common.extensions.setCommonInterceptor
import com.lee.playandroid.common.ui.widget.AppLoadResource
import com.lee.playandroid.service.hepler.ApplicationModuleService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * 项目自动初始化 进行模块、工具类、基础配置初始化
 * @author jv.lee
 * @date 2023/2/7
 */
class AutoInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            // 深色主题适配
            DarkModeTools.init(context)
            // 屏幕适配
            ScreenDensityTools.init(context)
            // 初始化网络拦截器
            HttpManager.instance.setCommonInterceptor()
            // 初始化缓存管理器
            CacheManager.init(context, BuildConfig.VERSION_CODE)
            // 子模块统一初始化
            ApplicationModuleService.init(context)
            // 全局统一loadPage资源样式设置
            ViewLoadManager.getInstance().setLoadResource(AppLoadResource())

            // 卡顿检测  compileSdk = 32 即android12 无法编译通过 (manifest <activity> has intent-filter exported = true )
//        if (BuildConfig.DEBUG) {
//            BlockCanary.install(context, AppBlockCanaryContext()).start()
//        }
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = Collections.emptyList()
}