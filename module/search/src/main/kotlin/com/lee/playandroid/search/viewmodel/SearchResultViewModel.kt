package com.lee.playandroid.search.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.uistate.LoadStatus
import com.lee.playandroid.base.uistate.UiStatePage
import com.lee.playandroid.base.uistate.UiStatePageMutableStateFlow
import com.lee.playandroid.base.uistate.UiStatePageStateFlow
import com.lee.playandroid.base.uistate.applyData
import com.lee.playandroid.base.uistate.pageLaunch
import com.lee.playandroid.base.viewmodel.BaseMVIViewModel
import com.lee.playandroid.base.viewmodel.IViewEvent
import com.lee.playandroid.base.viewmodel.IViewIntent
import com.lee.playandroid.base.viewmodel.IViewState
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.search.model.api.ApiService
import com.lee.playandroid.search.ui.SearchResultFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 搜索结果页viewModel
 * @author jv.lee
 * @date 2021/11/22
 */
class SearchResultViewModel(handle: SavedStateHandle) :
    BaseMVIViewModel<SearchResultViewState, IViewEvent, SearchResultViewIntent>() {

    private val key = handle[SearchResultFragment.ARG_PARAMS_SEARCH_KEY] ?: ""

    private val api = createApi<ApiService>()

    private val _searchResultFlow: UiStatePageMutableStateFlow =
        MutableStateFlow(UiStatePage.Default(0))
    val searchResultFlow: UiStatePageStateFlow = _searchResultFlow.asStateFlow()

    init {
        dispatch(SearchResultViewIntent.RequestPage(LoadStatus.INIT))
    }

    override fun initViewState() = SearchResultViewState(key)

    override fun dispatch(intent: SearchResultViewIntent) {
        when (intent) {
            is SearchResultViewIntent.RequestPage -> {
                requestSearch(intent.status)
            }
        }
    }

    /**
     * 根据搜索key获取搜索结果数据
     * @param status 分页请求状态
     */
    private fun requestSearch(@LoadStatus status: Int) {
        viewModelScope.launch {
            _searchResultFlow.pageLaunch(status, { page ->
                applyData { api.postSearchAsync(page, key).checkData() }
            })
        }
    }
}

data class SearchResultViewState(val title: String) : IViewState

sealed class SearchResultViewIntent : IViewIntent {
    data class RequestPage(@LoadStatus val status: Int) : SearchResultViewIntent()
}