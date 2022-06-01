package com.lee.playandroid.system.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.cacheFlow
import com.lee.playandroid.common.entity.NavigationItem
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.system.constants.Constants.CACHE_KEY_NAVIGATION_CONTENT
import com.lee.playandroid.system.model.api.ApiService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 体系第二个tab 导航页面 ViewModel
 * @author jv.lee
 * @date 2021/11/12
 */
class NavigationContentViewModel : ViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()

    private val _viewStates = MutableStateFlow(NavigationContentViewState())
    val viewStates: StateFlow<NavigationContentViewState> = _viewStates

    private val _viewEvents = Channel<NavigationContentViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: NavigationContentViewAction) {
        when (action) {
            is NavigationContentViewAction.RequestData -> {
                requestNavigationData()
            }
            is NavigationContentViewAction.SelectTabIndex -> {
                selectTabIndex(action.index)
            }
        }
    }

    /**
     * 该方法不再init调用，因为直接加载会导致页面卡顿，采用fragment懒加载方式
     */
    private fun requestNavigationData() {
        viewModelScope.launch {
            cacheManager.cacheFlow(CACHE_KEY_NAVIGATION_CONTENT) {
                api.getNavigationDataAsync().checkData().filter { it.articles.isNotEmpty() }
            }.onStart {
                _viewStates.update { it.copy(isLoading = true) }
            }.catch { error ->
                _viewStates.update { it.copy(isLoading = false) }
                _viewEvents.send(NavigationContentViewEvent.RequestFailed(error = error))
            }.collect { data ->
                _viewStates.update { it.copy(navigationItemList = data, isLoading = false) }
            }
        }

    }

    private fun selectTabIndex(index: Int) {
        _viewStates.update { it.copy(selectedTabIndex = index) }
    }

}

data class NavigationContentViewState(
    val isLoading: Boolean = true,
    val navigationItemList: List<NavigationItem> = emptyList(),
    val selectedTabIndex: Int = 0
)

sealed class NavigationContentViewEvent {
    data class RequestFailed(val error: Throwable) : NavigationContentViewEvent()
}

sealed class NavigationContentViewAction {
    object RequestData : NavigationContentViewAction()
    data class SelectTabIndex(val index: Int) : NavigationContentViewAction()
}