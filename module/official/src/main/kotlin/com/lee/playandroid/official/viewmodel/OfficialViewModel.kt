package com.lee.playandroid.official.viewmodel

import androidx.lifecycle.viewModelScope
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.cacheFlow
import com.lee.library.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.library.common.ui.base.BaseTabViewAction
import com.lee.playandroid.library.common.ui.base.BaseTabViewEvent
import com.lee.playandroid.library.common.ui.base.BaseTabViewState
import com.lee.playandroid.official.constants.Constants.CACHE_KEY_OFFICIAL_TAB
import com.lee.playandroid.official.model.api.ApiService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2021/11/8
 * @description 公众号Tab ViewModel
 */
class OfficialViewModel : CoroutineViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()

    private val _viewStates = MutableStateFlow(BaseTabViewState())
    val viewStates: StateFlow<BaseTabViewState> = _viewStates

    private val _viewEvents = Channel<BaseTabViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    init {
        dispatch(BaseTabViewAction.RequestData)
    }

    fun dispatch(action: BaseTabViewAction) {
        when (action) {
            is BaseTabViewAction.RequestData -> {
                requestTabs()
            }
        }
    }

    private fun requestTabs() {
        viewModelScope.launch {
            cacheManager.cacheFlow(CACHE_KEY_OFFICIAL_TAB) {
                api.getOfficialTabsAsync().checkData()
            }.onStart {
                _viewStates.update { it.copy(loading = true) }
            }.catch { error ->
                _viewEvents.send(BaseTabViewEvent.RequestFailed(error = error))
                _viewStates.update { it.copy(loading = false) }
            }.collect { data ->
                _viewStates.update { it.copy(loading = false, tabList = data) }
            }
        }
    }

}