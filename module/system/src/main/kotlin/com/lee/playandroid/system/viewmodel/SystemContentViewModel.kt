package com.lee.playandroid.system.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.cache.CacheManager
import com.lee.playandroid.base.extensions.cacheFlow
import com.lee.playandroid.common.entity.ParentTab
import com.lee.playandroid.common.entity.Tab
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.system.constants.Constants.CACHE_KEY_SYSTEM_CONTENT
import com.lee.playandroid.system.model.api.ApiService
import com.lee.playandroid.system.ui.SystemContentTabFragment
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 体系内容列表 ViewModel
 * @author jv.lee
 * @date 2021/11/10
 */
class SystemContentViewModel : ViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()

    private val _viewStates = MutableStateFlow(SystemContentViewState())
    val viewStates: StateFlow<SystemContentViewState> = _viewStates

    private val _viewEvents = Channel<SystemContentViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    init {
        dispatch(SystemContentViewAction.RequestData)
    }

    fun dispatch(action: SystemContentViewAction) {
        when (action) {
            is SystemContentViewAction.RequestData -> {
                requestParentTab()
            }
            is SystemContentViewAction.NavigationContentTab -> {
                navigationContentTab(action.tab)
            }
        }
    }

    private fun requestParentTab() {
        viewModelScope.launch {
            cacheManager.cacheFlow(CACHE_KEY_SYSTEM_CONTENT) {
                api.getParentTabAsync().checkData().filter { it.children.isNotEmpty() }
            }.catch { error ->
                _viewEvents.send(SystemContentViewEvent.RequestFailed(error = error))
            }.collect { data ->
                _viewStates.update { it.copy(isLoading = false, parentTabList = data) }
            }
        }
    }

    /**
     * 导航至目标页面
     * @param tab 数据item title:item.name ,data:item.children
     * @see SystemContentTabFragment
     */
    private fun navigationContentTab(tab: ParentTab) {
        viewModelScope.launch {
            val data = arrayListOf<Tab>().apply { addAll(tab.children) }
            val bundle = Bundle()
            bundle.putString(SystemContentTabFragment.ARG_PARAMS_TAB_TITLE, tab.name)
            bundle.putParcelableArrayList(SystemContentTabFragment.ARG_PARAMS_TAB_DATA, data)
            _viewEvents.send(SystemContentViewEvent.NavigationContentTabEvent(bundle = bundle))
        }
    }

}

data class SystemContentViewState(
    val isLoading: Boolean = true,
    val parentTabList: List<ParentTab> = emptyList()
)

sealed class SystemContentViewEvent {
    data class RequestFailed(val error: Throwable) : SystemContentViewEvent()
    data class NavigationContentTabEvent(val bundle: Bundle) : SystemContentViewEvent()
}

sealed class SystemContentViewAction {
    object RequestData : SystemContentViewAction()
    data class NavigationContentTab(val tab: ParentTab) : SystemContentViewAction()
}