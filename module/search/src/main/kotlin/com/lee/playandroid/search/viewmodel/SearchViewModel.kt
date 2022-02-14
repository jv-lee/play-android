package com.lee.playandroid.search.viewmodel

import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.stateUpdate
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.entity.SearchHistory
import com.lee.playandroid.search.db.SearchHistoryDatabase
import com.lee.playandroid.search.helper.SearchHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * @author jv.lee
 * @date 2021/11/19
 * @description
 */
class SearchViewModel : CoroutineViewModel() {

    private val _searchHotFlow = MutableStateFlow<UiState>(UiState.Default)
    val searchHotFlow: StateFlow<UiState> = _searchHotFlow.asStateFlow()

    private val _searchHistoryFlow = MutableStateFlow<UiState>(UiState.Default)
    val searchHistoryFlow: StateFlow<UiState> = _searchHistoryFlow.asStateFlow()

    /**
     * 获取搜索热门标签
     */
    private fun requestSearchHotData() {
        launchIO {
            _searchHotFlow.stateUpdate { SearchHelper.getHotCategory() }
        }
    }

    /**
     * 获取搜索历史数据
     */
    private fun requestSearchHistoryData() {
        launchIO {
            _searchHistoryFlow.stateUpdate {
                SearchHistoryDatabase.get().searchHistoryDao().querySearchHistory()
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

    /**
     * 删除搜索记录
     * @param key 被搜索的key
     */
    fun deleteSearchHistory(key: String) {
        launchIO {
            SearchHistoryDatabase.get().searchHistoryDao().delete(SearchHistory(key = key))
            requestSearchHistoryData()
        }
    }

    /**
     * 清空所有搜索记录
     */
    fun clearSearchHistory() {
        launchIO {
            SearchHistoryDatabase.get().searchHistoryDao().clearSearchHistory()
            requestSearchHistoryData()
        }
    }

    init {
        requestSearchHotData()
        requestSearchHistoryData()
    }

}