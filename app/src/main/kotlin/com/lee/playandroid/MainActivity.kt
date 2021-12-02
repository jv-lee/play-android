package com.lee.playandroid

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import com.lee.library.base.BaseActivity
import com.lee.library.extensions.banBackEvent
import com.lee.library.extensions.binding
import com.lee.library.tools.DarkModeTools
import com.lee.library.tools.ScreenDensityUtil
import com.lee.library.tools.StatusTools
import com.lee.playandroid.databinding.ActivityMainBinding
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService
import kotlinx.coroutines.*

/**
 * @author jv.lee
 * @date 2021/11/2
 * @description 程序主窗口 单Activity架构
 */
class MainActivity : BaseActivity(),
    CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private val binding by binding(ActivityMainBinding::inflate)

    private val backCallback = banBackEvent()

    override fun initSavedState(intent: Intent, savedInstanceState: Bundle?) {
        super.initSavedState(intent, savedInstanceState)
        if (savedInstanceState == null) {
            launch {
                //进程初始化启动 请求配置
                requestConfig()
                animVisibleUi(300)
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        launch {
            //程序以外重启 或重新创建MainActivity 无需获取配置，直接显示view
            animVisibleUi(0)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        ScreenDensityUtil.init(this)
        super.onConfigurationChanged(newConfig)

        if (DarkModeTools.get().isDarkTheme()) {
            StatusTools.setLightStatusIcon(this)
        } else {
            StatusTools.setDarkStatusIcon(this)
        }
    }

    override fun bindView() {
    }

    override fun bindData() {
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    //客户端入口读取APP配置
    private suspend fun requestConfig() {
        ModuleService.find<AccountService>().requestAccountInfo(this)
    }

    /**
     * 动画显示UI页面
     * @param duration 页面预加载时间
     */
    private suspend fun animVisibleUi(duration: Long) {
        //移除back事件禁用
        backCallback.remove()
        //预加载预留时间
        delay(duration)
        //设置动画显示rootView
        val anim = ObjectAnimator.ofFloat(0F, 1F)
        anim.duration = duration
        anim.interpolator = LinearInterpolator()
        anim.addUpdateListener {
            binding.mainContainer.alpha = it.animatedValue as Float
        }
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                binding.mainContainer.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                window.decorView.background = null
            }
        })
        anim.start()
    }
}