package com.lee.playandroid.system.viewmodel

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.cache.CacheManager
import com.lee.playandroid.base.extensions.cacheFlow
import com.lee.playandroid.base.viewmodel.BaseMVIViewModel
import com.lee.playandroid.base.viewmodel.IViewEvent
import com.lee.playandroid.base.viewmodel.IViewIntent
import com.lee.playandroid.base.viewmodel.IViewState
import com.lee.playandroid.common.entity.ParentTab
import com.lee.playandroid.common.entity.Tab
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.system.constants.Constants.CACHE_KEY_SYSTEM_CONTENT
import com.lee.playandroid.system.model.api.ApiService
import com.lee.playandroid.system.ui.SystemContentTabFragment
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 体系内容列表 viewModel
 * @author jv.lee
 * @date 2021/11/10
 */
class SystemContentViewModel :
    BaseMVIViewModel<SystemContentViewState, SystemContentViewEvent, SystemContentViewIntent>() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()

    init {
        dispatch(SystemContentViewIntent.RequestData)
    }

    override fun initViewState() = SystemContentViewState()

    override fun dispatch(intent: SystemContentViewIntent) {
        when (intent) {
            is SystemContentViewIntent.RequestData -> {
                requestParentTab()
            }

            is SystemContentViewIntent.NavigationContentTab -> {
                navigationContentTab(intent.tab)
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
) : IViewState

sealed class SystemContentViewEvent : IViewEvent {
    data class RequestFailed(val error: Throwable) : SystemContentViewEvent()
    data class NavigationContentTabEvent(val bundle: Bundle) : SystemContentViewEvent()
}

sealed class SystemContentViewIntent : IViewIntent {
    object RequestData : SystemContentViewIntent()
    data class NavigationContentTab(val tab: ParentTab) : SystemContentViewIntent()
}