package com.lee.playandroid.system.viewmodel

import androidx.lifecycle.viewModelScope
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.cacheFlow
import com.lee.library.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.entity.ParentTab
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.system.constants.Constants.CACHE_KEY_SYSTEM_CONTENT
import com.lee.playandroid.system.model.api.ApiService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2021/11/10
 * @description 体系内容列表 ViewModel
 */
class SystemContentViewModel : CoroutineViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()

    private val _viewStates = MutableStateFlow(SystemContentViewState())
    val viewStates: StateFlow<SystemContentViewState> = _viewStates

    private val _viewEvents = Channel<SystemContentViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    init {
        dispatch(SystemContentViewAction.RequestData)
    }

    fun dispatch(action: SystemContentViewAction) {
        when (action) {
            is SystemContentViewAction.RequestData -> {
                requestParentTab()
            }
        }
    }

    private fun requestParentTab() {
        viewModelScope.launch {
            cacheManager.cacheFlow(CACHE_KEY_SYSTEM_CONTENT) {
                api.getParentTabAsync().checkData().filter {
                    it.children.isNotEmpty()
                }
            }.catch { error ->
                _viewEvents.send(SystemContentViewEvent.RequestFailed(error = error))
            }.collect { data ->
                _viewStates.update { it.copy(isLoading = false, parentTabList = data) }
            }
        }
    }

}

data class SystemContentViewState(
    val isLoading: Boolean = true,
    val parentTabList: List<ParentTab> = emptyList()
)

sealed class SystemContentViewEvent {
    data class RequestFailed(val error: Throwable) : SystemContentViewEvent()
}

sealed class SystemContentViewAction {
    object RequestData : SystemContentViewAction()
}