package com.lee.playandroid.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.stateFlow
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.entity.SearchHistory
import com.lee.playandroid.search.db.SearchHistoryDatabase
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

    private val _searchHistoryLive = MutableLiveData<UiState>()
    val searchHistoryLive: LiveData<UiState> = _searchHistoryLive

    /**
     * 获取搜索热门标签
     */
    private fun requestSearchHotData() {
        launchIO {
            stateFlow {
                SearchHelper.getHotCategory()
            }.collect {
                _searchHotLive.postValue(it)
            }
        }
    }

    /**
     * 获取搜索历史数据
     */
    private fun requestSearchHistoryData() {
        launchIO {
            stateFlow {
                SearchHistoryDatabase.get().searchHistoryDao().querySearchHistory()
            }.collect {
                _searchHistoryLive.postValue(it)
            }
        }
    }

    /**
     * 添加搜索记录
     * @param key 被搜索的key
     */
    fun addSearchHistory(key: String) {
        launchIO {
            SearchHistoryDatabase.get().searchHistoryDao().insert(SearchHistory(key = key))
            requestSearchHistoryData()
        }
    }

    init {
        requestSearchHotData()
        requestSearchHistoryData()
    }

}