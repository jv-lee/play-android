package com.lee.playandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.R
import com.lee.playandroid.base.base.ApplicationExtensions.app
import com.lee.playandroid.service.AccountService
import com.lee.playandroid.service.hepler.ModuleService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 闪屏viewModel
 * @author jv.lee
 * @date 2022/3/28
 */
class SplashViewModel : ViewModel() {

    val accountService = ModuleService.find<AccountService>()

    private val _viewStates = MutableStateFlow(SplashViewState())
    val viewStates: StateFlow<SplashViewState> = _viewStates

    private val _viewEvents = Channel<SplashViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(intent: SplashViewIntent) {
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

data class SplashViewState(val timeText: String = "")

sealed class SplashViewEvent {
    data class NavigationMainEvent(val duration: Long) : SplashViewEvent()
    data class VisibleSplashEvent(val splashAdRes: Int) : SplashViewEvent()
}

sealed class SplashViewIntent {
    object RequestSplashAd : SplashViewIntent()
    data class NavigationMain(val duration: Long = 0) : SplashViewIntent()
}