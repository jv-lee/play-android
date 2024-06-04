package com.lee.playandroid.viewmodel

import androidx.lifecycle.viewModelScope
import com.lee.playandroid.R
import com.lee.playandroid.base.base.ApplicationExtensions.app
import com.lee.playandroid.base.viewmodel.BaseMVIViewModel
import com.lee.playandroid.base.viewmodel.IViewEvent
import com.lee.playandroid.base.viewmodel.IViewIntent
import com.lee.playandroid.base.viewmodel.IViewState
import com.lee.playandroid.service.AccountService
import com.lee.playandroid.service.hepler.ModuleService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 闪屏viewModel
 * @author jv.lee
 * @date 2022/3/28
 */
class SplashViewModel : BaseMVIViewModel<SplashViewState, SplashViewEvent, SplashViewIntent>() {

    val accountService = ModuleService.find<AccountService>()

    override fun initViewState() = SplashViewState()

    override fun dispatch(intent: SplashViewIntent) {
        when (intent) {
            is SplashViewIntent.RequestSplashAd -> {
                requestSplashAd()
            }

            is SplashViewIntent.NavigationMain -> {
                navigationMain(intent.duration)
            }
        }
    }

    private fun requestSplashAd() {
        viewModelScope.launch {
            // 此处可加入splash广告资源获取逻辑
            val splashAdRes = R.mipmap.splash_ad
            flowOf(5, 4, 3, 2, 1)
                .onStart { _viewEvents.send(SplashViewEvent.VisibleSplashEvent(splashAdRes)) }
                .onCompletion { navigationMain(300) }
                .collect { time ->
                    val timeText = app.getString(R.string.splash_time_text, time)
                    _viewStates.update { it.copy(timeText = timeText) }
                    delay(1000)
                }
        }
    }

    private fun navigationMain(duration: Long) {
        viewModelScope.launch {
            _viewEvents.send(SplashViewEvent.NavigationMainEvent(duration))
        }
    }
}

data class SplashViewState(val timeText: String = "") : IViewState

sealed class SplashViewEvent : IViewEvent {
    data class NavigationMainEvent(val duration: Long) : SplashViewEvent()
    data class VisibleSplashEvent(val splashAdRes: Int) : SplashViewEvent()
}

sealed class SplashViewIntent : IViewIntent {
    object RequestSplashAd : SplashViewIntent()
    data class NavigationMain(val duration: Long = 0) : SplashViewIntent()
}