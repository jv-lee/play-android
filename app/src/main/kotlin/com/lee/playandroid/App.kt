package com.lee.playandroid

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.lee.playandroid.base.adapter.manager.ViewLoadManager
import com.lee.playandroid.base.base.BaseApplication
import com.lee.playandroid.base.cache.CacheManager
import com.lee.playandroid.base.extensions.bindFragmentLifecycle
import com.lee.playandroid.base.extensions.unbindFragmentLifecycle
import com.lee.playandroid.base.interadp.SimpleActivityLifecycleCallbacks
import com.lee.playandroid.base.interadp.SimpleFragmentLifecycleCallbacks
import com.lee.playandroid.base.net.HttpManager
import com.lee.playandroid.base.tools.DarkModeTools
import com.lee.playandroid.base.tools.ScreenDensityUtil
import com.lee.playandroid.common.extensions.appThemeSet
import com.lee.playandroid.common.extensions.runInternalBlock
import com.lee.playandroid.common.extensions.setCommonInterceptor
import com.lee.playandroid.common.ui.widget.AppLoadResource
import com.lee.playandroid.service.hepler.ApplicationModuleService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 程序主入口
 * 进行模块、工具类、基础配置初始化 activity/fragment生命周期统一监听功能业务处理
 * @author jv.lee
 * @date 2021/11/2
 */
class App : BaseApplication() {

    private val fragmentLifecycleCallbacks = object : SimpleFragmentLifecycleCallbacks() {

        override fun onFragmentCreated(
            fm: FragmentManager,
            f: Fragment,
            savedInstanceState: Bundle?
        ) {
            ScreenDensityUtil.init(f.requireActivity())
            super.onFragmentCreated(fm, f, savedInstanceState)
        }
    }

    private val activityLifecycleCallbacks = object : SimpleActivityLifecycleCallbacks() {

        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            // 过滤项目外的activity
            activity.runInternalBlock {
                activity.appThemeSet()
                activity.bindFragmentLifecycle(fragmentLifecycleCallbacks)
            }
            super.onActivityCreated(activity, bundle)
        }

        override fun onActivityDestroyed(activity: Activity) {
            activity.runInternalBlock {
                ScreenDensityUtil.resetDensity(activity)
                activity.unbindFragmentLifecycle(fragmentLifecycleCallbacks)
            }
            super.onActivityDestroyed(activity)
        }

        override fun onActivityResumed(activity: Activity) {
            activity.runInternalBlock { ScreenDensityUtil.init(activity) }
            super.onActivityResumed(activity)
        }

    }

    override fun init() {
        CoroutineScope(Dispatchers.IO).launch {
            // 深色主题适配
            DarkModeTools.init(applicationContext)
            // 屏幕适配
            ScreenDensityUtil.init(this@App)
            // 初始化网络拦截器
            HttpManager.instance.setCommonInterceptor()
            // 初始化缓存管理器
            CacheManager.init(applicationContext, BuildConfig.VERSION_CODE)
            // 子模块统一初始化
            ApplicationModuleService.init(this@App)
            // 全局统一loadPage资源样式设置
            ViewLoadManager.getInstance().setLoadResource(AppLoadResource())

            // 卡顿检测  compileSdk = 32 即android12 无法编译通过 (manifest <activity> has intent-filter exported = true )
//            if (BuildConfig.DEBUG) {
//                BlockCanary.install(this@App, AppBlockCanaryContext()).start()
//            }

            // 注册Activity生命周期监听
            registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        }
    }

    override fun unInit() {
        unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }
}