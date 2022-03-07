package com.lee.playandroid

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import com.lee.library.base.BaseActivity
import com.lee.library.extensions.*
import com.lee.library.tools.DarkModeTools
import com.lee.library.tools.ScreenDensityUtil
import com.lee.library.tools.StatusTools.setDarkStatusIcon
import com.lee.library.tools.StatusTools.setLightStatusIcon
import com.lee.library.tools.StatusTools.setNavigationBarColor
import com.lee.library.utils.LogUtil
import com.lee.playandroid.databinding.ActivityMainBinding
import com.lee.playandroid.databinding.LayoutStubMainBinding
import com.lee.playandroid.databinding.LayoutStubSplashBinding
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * @author jv.lee
 * @date 2021/11/2
 * @description 程序主窗口 单Activity架构
 */
class MainActivity : BaseActivity(),
    CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private val backCallback = banBackEvent()

    private val binding by binding(ActivityMainBinding::inflate)

    private val splashBinding by lazy {
        val splash = binding.root.findViewById<ViewStub>(R.id.view_stub_splash).inflate()
        LayoutStubSplashBinding.bind(splash)
    }

    override fun initSavedState(intent: Intent, savedInstanceState: Bundle?) {
        super.initSavedState(intent, savedInstanceState)
        if (savedInstanceState == null) {
            launch {
                // 进程初始化启动 请求配置
                requestConfig()

                // 发起闪屏广告逻辑
                val splashResult = async { requestSplashAd() }
                splashResult.invokeOnCompletion { animVisibleUi(300) }
                splashResult.await()
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        launch {
            if (BuildConfig.DEBUG) {
                toast("onRestoreInstance")
            }
            //程序以外重启 或重新创建MainActivity 无需获取配置，直接显示view
            animVisibleUi()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (BuildConfig.DEBUG) {
            toast("onConfigurationChanged")
        }
        ScreenDensityUtil.init(this)
        super.onConfigurationChanged(newConfig)

        if (DarkModeTools.get().isDarkTheme()) {
            setNavigationBarColor(Color.BLACK)
            setLightStatusIcon()
        } else {
            setNavigationBarColor(Color.WHITE)
            setDarkStatusIcon()
        }
    }

    override fun onResume() {
        super.onResume()
        if (BuildConfig.DEBUG) {
            val isSystem = DarkModeTools.get().isSystemTheme()
            val isDark = DarkModeTools.get().isDarkTheme()
            LogUtil.i("isSystem:$isSystem,isDark:$isDark")
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

    /**
     * 客户端入口读取APP配置
     */
    private suspend fun requestConfig() {
        ModuleService.find<AccountService>().requestAccountInfo(this)
    }

    /**
     * 闪屏广告逻辑
     */
    private suspend fun requestSplashAd() {
        coroutineScope {
            if (BuildConfig.DEBUG) {
                // 闪屏图片加载动画
                val splashAnimation =
                    AnimationUtils.loadAnimation(this@MainActivity, R.anim.alpha_in).apply {
                        startListener {
                            splashBinding.tvTime.visibility = View.VISIBLE
                            splashBinding.ivPicture.setImageResource(R.mipmap.splash_ad)
                        }
                    }

                // 启动动画及取消处理
                splashBinding.container.startAnimation(splashAnimation)
                splashBinding.tvTime.setOnClickListener { cancel() }

                // 倒计时更改view显示
                flowOf(5, 4, 3, 2, 1).collect {
                    splashBinding.tvTime.text = getString(R.string.splash_time_text, it)
                    delay(1000)
                }
            }
        }
    }

    /**
     * 动画显示UI页面
     */
    private fun animVisibleUi(duration: Long = 0) {
        val main = binding.root.findViewById<ViewStub>(R.id.view_stub_main).inflate()
        LayoutStubMainBinding.bind(main)

        //移除back事件禁用
        backCallback.remove()
        //设置动画显示rootView
        val anim = ObjectAnimator.ofFloat(0F, 1F)
        anim.startDelay = duration
        anim.duration = duration
        anim.interpolator = LinearInterpolator()
        anim.addUpdateListener {
            binding.mainContainer.alpha = it.animatedValue as Float
            splashBinding.root.alpha = 1F - (it.animatedValue as Float)
        }
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                binding.mainContainer.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                binding.root.removeView(splashBinding.root)
                window.decorView.background = null
            }
        })
        anim.start()
    }

}