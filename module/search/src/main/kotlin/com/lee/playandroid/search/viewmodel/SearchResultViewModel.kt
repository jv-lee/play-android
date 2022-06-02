package com.lee.playandroid.search.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.viewstate.*
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.search.model.api.ApiService
import com.lee.playandroid.search.ui.SearchResultFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 *
 * @author jv.lee
 * @date 2021/11/22
 */
class SearchResultViewModel(handle: SavedStateHandle) : ViewModel() {

    private val key = handle[SearchResultFragment.ARG_PARAMS_SEARCH_KEY] ?: ""

    private val api = createApi<ApiService>()

    private val _viewStates = MutableStateFlow(SearchResultViewState(key))
    val viewStates: StateFlow<SearchResultViewState> = _viewStates

    private val _searchResultFlow: UiStatePageMutableStateFlow =
        MutableStateFlow(UiStatePage.Default(0))
    val searchResultFlow: UiStatePageStateFlow = _searchResultFlow.asStateFlow()

    init {
        dispatch(SearchResultViewAction.RequestPage(LoadStatus.INIT))
    }

    fun dispatch(action: SearchResultViewAction) {
        when (action) {
            is SearchResultViewAction.RequestPage -> {
                requestSearch(action.status)
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

data class SearchResultViewState(val title: String)

sealed class SearchResultViewAction {
    data class RequestPage(@LoadStatus val status: Int) : SearchResultViewAction()
}