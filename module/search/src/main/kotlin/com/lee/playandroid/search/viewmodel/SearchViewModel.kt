package com.lee.playandroid.search.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.cache.CacheManager
import com.lee.playandroid.base.extensions.cacheFlow
import com.lee.playandroid.common.entity.SearchHistory
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.search.constants.Constants
import com.lee.playandroid.search.model.api.ApiService
import com.lee.playandroid.search.model.db.SearchDatabase
import com.lee.playandroid.search.model.entity.SearchHotUI
import com.lee.playandroid.search.ui.SearchResultFragment
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 搜索页面viewModel
 * @author jv.lee
 * @date 2021/11/19
 */
class SearchViewModel : ViewModel() {

    private val api = createApi<ApiService>()
    private val searchHistoryDao = SearchDatabase.get().searchHistoryDao()
    private val cacheManager = CacheManager.getDefault()

    private val _viewStates = MutableStateFlow(SearchViewState())
    val viewStates: Flow<SearchViewState> = _viewStates

    private val _viewEvents = Channel<SearchViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    init {
        requestSearchHotData()
        requestSearchHistoryData()
    }

    fun dispatch(intent: SearchViewIntent) {
        when (intent) {
            is SearchViewIntent.NavigationSearchResult -> {
                navigationSearchKey(intent.key)
            }
            is SearchViewIntent.AddHistory -> {
                addSearchHistory(intent.key)
            }
            is SearchViewIntent.DeleteHistory -> {
                deleteSearchHistory(intent.key)
            }
            is SearchViewIntent.ClearHistory -> {
                clearSearchHistory()
            }
        }
    }

    /**
     * 获取搜索热门标签
     */
    private fun requestSearchHotData() {
        viewModelScope.launch {
            cacheManager.cacheFlow(Constants.CACHE_KEY_SEARCH_HOT) {
                api.getSearchHotAsync().checkData()
            }.map { list ->
                list.map { SearchHotUI(it.name) }
            }.catch { error ->
                _viewEvents.send(SearchViewEvent.FailedEvent(error = error))
            }.collect { data ->
                _viewStates.update { it.copy(searchHotList = data) }
            }
        }
    }

    /**
     * 获取搜索历史数据
     */
    private fun requestSearchHistoryData() {
        viewModelScope.launch {
            flow {
                emit(searchHistoryDao.querySearchHistory())
            }.catch { error ->
                _viewEvents.send(SearchViewEvent.FailedEvent(error = error))
            }.collect { data ->
                _viewStates.update { it.copy(searchHistoryList = data) }
            }
        }
    }

    /**
     * 导航到搜索结果页
     * @param key 搜索key
     */
    private fun navigationSearchKey(key: String) {
        viewModelScope.launch {
            // 添加历史记录隐藏软键盘
            addSearchHistory(key)

            val bundle = Bundle()
            bundle.putString(SearchResultFragment.ARG_PARAMS_SEARCH_KEY, key)
            _viewEvents.send(SearchViewEvent.NavigationSearchResultEvent(bundle))
        }
    }

    /**
     * 添加搜索记录
     * @param key 被搜索的key
     */
    private fun addSearchHistory(key: String) {
        viewModelScope.launch {
            searchHistoryDao.insert(SearchHistory(key = key))
            requestSearchHistoryData()
        }
    }

    /**
     * 删除搜索记录
     * @param key 被搜索的key
     */
    private fun deleteSearchHistory(key: String) {
        viewModelScope.launch {
            searchHistoryDao.delete(SearchHistory(key = key))
            requestSearchHistoryData()
        }
    }

    /**
     * 清空所有搜索记录
     */
    private fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryDao.clearSearchHistory()
            requestSearchHistoryData()
        }
    }
}

data class SearchViewState(
    val searchHotList: List<SearchHotUI> = emptyList(),
    val searchHistoryList: List<SearchHistory> = emptyList()
)

sealed class SearchViewEvent {
    data class NavigationSearchResultEvent(val bundle: Bundle) : SearchViewEvent()
    data class FailedEvent(val error: Throwable) : SearchViewEvent()
}

sealed class SearchViewIntent {
    data class NavigationSearchResult(val key: String) : SearchViewIntent()
    data class AddHistory(val key: String) : SearchViewIntent()
    data class DeleteHistory(val key: String) : SearchViewIntent()
    object ClearHistory : SearchViewIntent()
}