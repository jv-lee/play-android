package com.lee.playandroid

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewStub
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.activity.viewModels
import com.lee.library.base.BaseActivity
import com.lee.library.extensions.banBackEvent
import com.lee.library.extensions.binding
import com.lee.library.extensions.launchAndRepeatWithViewLifecycle
import com.lee.library.extensions.startListener
import com.lee.library.tools.DarkModeTools
import com.lee.library.tools.ScreenDensityUtil
import com.lee.library.viewstate.collectState
import com.lee.playandroid.databinding.ActivityMainBinding
import com.lee.playandroid.databinding.LayoutStubMainBinding
import com.lee.playandroid.databinding.LayoutStubSplashBinding
import com.lee.playandroid.library.common.extensions.appThemeSet
import com.lee.playandroid.viewmodel.SplashViewAction
import com.lee.playandroid.viewmodel.SplashViewEvent
import com.lee.playandroid.viewmodel.SplashViewModel
import com.lee.playandroid.viewmodel.SplashViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2021/11/2
 * @description 程序主窗口 单Activity架构
 */
class MainActivity : BaseActivity(),
    CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private val backCallback = banBackEvent()

    private val viewModel by viewModels<SplashViewModel>()

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
                viewModel.accountService.requestAccountInfo(this@MainActivity)

                // 发起闪屏广告逻辑
                viewModel.dispatch(SplashViewAction.RequestSplashAd)
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        launch {
            //程序以外重启 或重新创建MainActivity 无需获取配置，直接显示view
            viewModel.dispatch(SplashViewAction.NavigationMain())
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // 屏幕适配 / 深色主题适配
        ScreenDensityUtil.init(this)
        DarkModeTools.init(applicationContext)
        appThemeSet()
        super.onConfigurationChanged(newConfig)
    }

    override fun bindView() {
    }

    override fun bindData() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is SplashViewEvent.NavigationMainEvent -> {
                        animVisibleUi(event.duration)
                    }
                }
            }
        }

        viewModel.viewStates.run {
            launchAndRepeatWithViewLifecycle {
                collectState(SplashViewState::splashAdVisible) { visible ->
                    if (visible) {
                        animVisibleSplash()
                    }
                }
            }
            launchAndRepeatWithViewLifecycle {
                collectState(SplashViewState::timeText) {
                    splashBinding.tvTime.text = it
                }
            }
        }
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    /**
     * 动画显示splash页面
     */
    private fun animVisibleSplash() {
        val splashAnimation =
            AnimationUtils.loadAnimation(this@MainActivity, R.anim.alpha_in).apply {
                startListener {
                    splashBinding.tvTime.visibility = View.VISIBLE
                    splashBinding.ivPicture.setImageResource(R.mipmap.splash_ad)
                }
            }
        splashBinding.container.startAnimation(splashAnimation)
        splashBinding.tvTime.setOnClickListener {
            viewModel.dispatch(SplashViewAction.NavigationMain(300))
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