package com.lee.playandroid.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.stateFlow
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.search.helper.SearchHelper
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/11/19
 * @description
 */
class SearchViewModel : CoroutineViewModel() {

    private val _searchHotLive = MutableLiveData<UiState>()
    val searchHotLive: LiveData<UiState> = _searchHotLive

    private fun requestSearchHotData() {
        launchIO {
            stateFlow {
                SearchHelper.getHomeCategory()
            }.collect {
                _searchHotLive.postValue(it)
            }
        }
    }

    init {
        requestSearchHotData()
    }

}