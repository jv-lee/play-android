package com.lee.playandroid.search.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.search.constants.Constants
import com.lee.playandroid.search.model.repository.ApiRepository

/**
 * @author jv.lee
 * @date 2021/11/22
 * @description
 */
class SearchResultViewModel(handle: SavedStateHandle) : CoroutineViewModel() {

    private val key = handle[Constants.ARG_PARAMS_SEARCH_KEY] ?: ""

    private val repository = ApiRepository()

    val searchResultLive = UiStatePageLiveData(0)

    fun requestSearch(@LoadStatus status: Int) {
        launchIO {
            searchResultLive.pageLaunch(status, { page ->
                applyData { repository.api.postSearchAsync(page, key).checkData() }
            })
        }
    }

    init {
        requestSearch(LoadStatus.INIT)
    }

}