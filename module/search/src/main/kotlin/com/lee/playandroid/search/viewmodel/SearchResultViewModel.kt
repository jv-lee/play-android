package com.lee.playandroid.search.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.lee.library.mvvm.ui.*
import com.lee.library.viewmodel.CoroutineViewModel
import com.lee.library.viewstate.*
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.search.model.api.ApiService
import com.lee.playandroid.search.ui.SearchResultFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * @author jv.lee
 * @date 2021/11/22
 * @description
 */
class SearchResultViewModel(handle: SavedStateHandle) : CoroutineViewModel() {

    private val key = handle[SearchResultFragment.ARG_PARAMS_SEARCH_KEY] ?: ""

    private val api = createApi<ApiService>()

    private val _searchResultFlow: UiStatePageMutableStateFlow =
        MutableStateFlow(_root_ide_package_.com.lee.library.viewstate.UiStatePage.Default(0))
    val searchResultFlow: UiStatePageStateFlow = _searchResultFlow.asStateFlow()

    init {
        dispatch(SearchResultViewAction.RequestPage(_root_ide_package_.com.lee.library.viewstate.LoadStatus.INIT))
    }

    fun dispatch(action: SearchResultViewAction) {
        when (action) {
            is SearchResultViewAction.RequestPage -> {
                requestSearch(action.status)
            }
        }
    }

    private fun requestSearch(@_root_ide_package_.com.lee.library.viewstate.LoadStatus status: Int) {
        launchIO {
            _searchResultFlow.pageLaunch(status, { page ->
                applyData { api.postSearchAsync(page, key).checkData() }
            })
        }
    }

}

sealed class SearchResultViewAction {
    data class RequestPage(@_root_ide_package_.com.lee.library.viewstate.LoadStatus val status: Int) : SearchResultViewAction()
}