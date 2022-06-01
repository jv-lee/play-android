package com.lee.playandroid.search.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.common.entity.SearchHistory
import com.lee.playandroid.search.model.db.SearchHistoryDatabase
import com.lee.playandroid.search.model.entity.SearchHot
import com.lee.playandroid.search.ui.SearchResultFragment
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 *
 * @author jv.lee
 * @date 2021/11/19
 */
class SearchViewModel : ViewModel() {

    private val _viewStates = MutableStateFlow(SearchViewState())
    val viewStates: Flow<SearchViewState> = _viewStates

    private val _viewEvents = Channel<SearchViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    init {
        requestSearchHotData()
        requestSearchHistoryData()
    }

    fun dispatch(action: SearchViewAction) {
        when (action) {
            is SearchViewAction.NavigationSearchResult -> {
                navigationSearchKey(action.key)
            }
            is SearchViewAction.AddHistory -> {
                addSearchHistory(action.key)
            }
            is SearchViewAction.DeleteHistory -> {
                deleteSearchHistory(action.key)
            }
            is SearchViewAction.ClearHistory -> {
                clearSearchHistory()
            }
        }
    }

    /**
     * 获取搜索热门标签
     */
    private fun requestSearchHotData() {
        viewModelScope.launch {
            flow {
                emit(SearchHot.getHotCategory())
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
                emit(SearchHistoryDatabase.get().searchHistoryDao().querySearchHistory())
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
            _viewStates.update { it.copy(hideKeyboard = true) }

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
            SearchHistoryDatabase.get().searchHistoryDao().insert(SearchHistory(key = key))
            requestSearchHistoryData()
        }
    }

    /**
     * 删除搜索记录
     * @param key 被搜索的key
     */
    private fun deleteSearchHistory(key: String) {
        viewModelScope.launch {
            SearchHistoryDatabase.get().searchHistoryDao().delete(SearchHistory(key = key))
            requestSearchHistoryData()
        }
    }

    /**
     * 清空所有搜索记录
     */
    private fun clearSearchHistory() {
        viewModelScope.launch {
            SearchHistoryDatabase.get().searchHistoryDao().clearSearchHistory()
            requestSearchHistoryData()
        }
    }

}

data class SearchViewState(
    val hideKeyboard: Boolean = false,
    val searchHotList: List<SearchHot> = emptyList(),
    val searchHistoryList: List<SearchHistory> = emptyList(),
)

sealed class SearchViewEvent {
    data class NavigationSearchResultEvent(val bundle: Bundle) : SearchViewEvent()
    data class FailedEvent(val error: Throwable) : SearchViewEvent()
}

sealed class SearchViewAction {
    data class NavigationSearchResult(val key: String) : SearchViewAction()
    data class AddHistory(val key: String) : SearchViewAction()
    data class DeleteHistory(val key: String) : SearchViewAction()
    object ClearHistory : SearchViewAction()
}