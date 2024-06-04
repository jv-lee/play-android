package com.lee.playandroid.system.viewmodel

import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.cache.CacheManager
import com.lee.playandroid.base.extensions.cacheFlow
import com.lee.playandroid.base.viewmodel.BaseMVIViewModel
import com.lee.playandroid.base.viewmodel.IViewEvent
import com.lee.playandroid.base.viewmodel.IViewIntent
import com.lee.playandroid.base.viewmodel.IViewState
import com.lee.playandroid.common.entity.NavigationItem
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.system.constants.Constants.CACHE_KEY_NAVIGATION_CONTENT
import com.lee.playandroid.system.model.api.ApiService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 导航内容列表 viewModel
 * @author jv.lee
 * @date 2021/11/12
 */
class NavigationContentViewModel :
    BaseMVIViewModel<NavigationContentViewState, NavigationContentViewEvent, NavigationContentViewIntent>() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()

    override fun initViewState() = NavigationContentViewState()

    override fun dispatch(intent: NavigationContentViewIntent) {
        when (intent) {
            is NavigationContentViewIntent.RequestData -> {
                requestNavigationData()
            }

            is NavigationContentViewIntent.SelectTabIndex -> {
                selectTabIndex(intent.index)
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
) : IViewState

sealed class NavigationContentViewEvent : IViewEvent {
    data class RequestFailed(val error: Throwable) : NavigationContentViewEvent()
}

sealed class NavigationContentViewIntent : IViewIntent {
    object RequestData : NavigationContentViewIntent()
    data class SelectTabIndex(val index: Int) : NavigationContentViewIntent()
}