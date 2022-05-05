package com.lee.playandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.library.base.ApplicationExtensions.app
import com.lee.playandroid.R
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/3/28
 * @description
 */
class SplashViewModel : ViewModel() {

    val accountService = ModuleService.find<AccountService>()

    private val _viewStates = MutableStateFlow(SplashViewState())
    val viewStates: StateFlow<SplashViewState> = _viewStates

    private val _viewEvents = Channel<SplashViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: SplashViewAction) {
        when (action) {
            is SplashViewAction.RequestSplashAd -> {
                playTime()
            }
            is SplashViewAction.NavigationMain -> {
                navigationMain(action.duration)
            }
        }
    }

    private fun playTime() {
        viewModelScope.launch {
            flowOf(5, 4, 3, 2, 1)
                .onStart { _viewStates.update { it.copy(splashAdVisible = true) } }
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

data class SplashViewState(
    val splashAdVisible: Boolean = false,
    val timeText: String = "",
)

sealed class SplashViewEvent {
    data class NavigationMainEvent(val duration: Long) : SplashViewEvent()
}

sealed class SplashViewAction {
    object RequestSplashAd : SplashViewAction()
    data class NavigationMain(val duration: Long = 0) : SplashViewAction()
}