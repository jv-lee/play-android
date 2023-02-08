package com.lee.playandroid

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.lee.playandroid.base.base.BaseApplication
import com.lee.playandroid.base.extensions.bindFragmentLifecycle
import com.lee.playandroid.base.extensions.unbindFragmentLifecycle
import com.lee.playandroid.base.interadp.SimpleActivityLifecycleCallbacks
import com.lee.playandroid.base.interadp.SimpleFragmentLifecycleCallbacks
import com.lee.playandroid.base.tools.ScreenDensityTools
import com.lee.playandroid.common.extensions.appThemeSet
import com.lee.playandroid.common.extensions.runInternalBlock

/**
 * 程序主入口
 * activity/fragment生命周期统一监听功能业务处理
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
            ScreenDensityTools.init(f.requireActivity())
            super.onFragmentCreated(fm, f, savedInstanceState)
        }
    }

    private val activityLifecycleCallbacks = object : SimpleActivityLifecycleCallbacks() {

        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            // 过滤项目外的activity
            activity.runInternalBlock {
                activity.bindFragmentLifecycle(fragmentLifecycleCallbacks)
            }
            super.onActivityCreated(activity, bundle)
        }

        override fun onActivityDestroyed(activity: Activity) {
            activity.runInternalBlock {
                ScreenDensityTools.resetDensity(activity)
                activity.unbindFragmentLifecycle(fragmentLifecycleCallbacks)
            }
            super.onActivityDestroyed(activity)
        }

        override fun onActivityResumed(activity: Activity) {
            activity.runInternalBlock {
                ScreenDensityTools.init(activity)
                activity.appThemeSet()
            }
            super.onActivityResumed(activity)
        }
    }

    override fun init() {
        // 注册Activity生命周期监听
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    override fun unInit() {
        unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }
}