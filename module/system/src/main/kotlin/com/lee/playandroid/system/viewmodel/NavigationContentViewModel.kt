package com.lee.playandroid.system.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.cacheFlow
import com.lee.library.viewstate.LoadStatus
import com.lee.playandroid.library.common.entity.NavigationItem
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.system.constants.Constants.CACHE_KEY_NAVIGATION_CONTENT
import com.lee.playandroid.system.model.api.ApiService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2021/11/12
 * @description 体系第二个tab 导航页面 ViewModel
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
                requestNavigationData(action.status)
            }
            is NavigationContentViewAction.SelectTabIndex -> {
                selectTabIndex(action.index)
            }
        }
    }

    /**
     * 该方法不再init调用，因为直接加载会导致页面卡顿，采用fragment懒加载方式
     */
    private fun requestNavigationData(@LoadStatus status: Int) {
        viewModelScope.launch {
            cacheManager.cacheFlow(CACHE_KEY_NAVIGATION_CONTENT) {
                api.getNavigationDataAsync().checkData()
                    .filter { it.articles.isNotEmpty() }
            }.catch { error ->
                _viewEvents.send(NavigationContentViewEvent.RequestFailed(error = error))
            }.collect { data ->
                _viewStates.update { it.copy(navigationItemList = data) }
            }
        }

    }

    private fun selectTabIndex(index: Int) {
        _viewStates.update { it.copy(selectedTabIndex = index) }
    }

}

data class NavigationContentViewState(
    val navigationItemList: List<NavigationItem> = emptyList(),
    val selectedTabIndex: Int = 0
)

sealed class NavigationContentViewEvent {
    data class RequestFailed(val error: Throwable) : NavigationContentViewEvent()
}

sealed class NavigationContentViewAction {
    data class RequestData(@LoadStatus val status: Int) : NavigationContentViewAction()
    data class SelectTabIndex(val index: Int) : NavigationContentViewAction()
}