package com.lee.playandroid.project.viewmodel

import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.cache.CacheManager
import com.lee.playandroid.base.extensions.cacheFlow
import com.lee.playandroid.base.viewmodel.BaseMVIViewModel
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.common.ui.base.BaseTabViewEvent
import com.lee.playandroid.common.ui.base.BaseTabViewIntent
import com.lee.playandroid.common.ui.base.BaseTabViewState
import com.lee.playandroid.project.constants.Constants.CACHE_KEY_PROJECT_TAB
import com.lee.playandroid.project.model.api.ApiService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 项目Tab ViewModel
 * @author jv.lee
 * @date 2021/11/9
 */
class ProjectViewModel : BaseMVIViewModel<BaseTabViewState, BaseTabViewEvent, BaseTabViewIntent>() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()

    init {
        dispatch(BaseTabViewIntent.RequestData)
    }

    override fun initViewState() = BaseTabViewState()

    override fun dispatch(intent: BaseTabViewIntent) {
        when (intent) {
            is BaseTabViewIntent.RequestData -> {
                requestTabs()
            }
        }
    }

    private fun requestTabs() {
        viewModelScope.launch {
            cacheManager.cacheFlow(CACHE_KEY_PROJECT_TAB) {
                api.getProjectTabsAsync().checkData()
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