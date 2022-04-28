package com.lee.playandroid.search.viewmodel

import androidx.lifecycle.SavedStateHandle
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
        MutableStateFlow(UiStatePage.Default(0))
    val searchResultFlow: UiStatePageStateFlow = _searchResultFlow.asStateFlow()

    fun requestSearch(@LoadStatus status: Int) {
        launchIO {
            _searchResultFlow.pageLaunch(status, { page ->
                applyData { api.postSearchAsync(page, key).checkData() }
            })
        }
    }

    init {
        requestSearch(LoadStatus.INIT)
    }

}