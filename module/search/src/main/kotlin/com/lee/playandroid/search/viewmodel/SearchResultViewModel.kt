package com.lee.playandroid.search.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.PageUiState
import com.lee.library.mvvm.ui.applyData
import com.lee.library.mvvm.ui.pageLaunch
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.search.constants.Constants
import com.lee.playandroid.search.model.repository.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * @author jv.lee
 * @date 2021/11/22
 * @description
 */
class SearchResultViewModel(handle: SavedStateHandle) : CoroutineViewModel() {

    private val key = handle[Constants.ARG_PARAMS_SEARCH_KEY] ?: ""

    private val repository = ApiRepository()

    private val _searchResultFlow = MutableStateFlow<PageUiState>(PageUiState.Loading(0))
    val searchResultFlow: StateFlow<PageUiState> = _searchResultFlow.asStateFlow()

    fun requestSearch(@LoadStatus status: Int) {
        launchIO {
            _searchResultFlow.pageLaunch(status, { page ->
                applyData { repository.api.postSearchAsync(page, key).checkData() }
            })
        }
    }

    init {
        requestSearch(LoadStatus.INIT)
    }

}