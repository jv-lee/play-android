package com.lee.playandroid.system.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.uistate.LoadStatus
import com.lee.playandroid.base.uistate.UiStatePage
import com.lee.playandroid.base.uistate.UiStatePageMutableStateFlow
import com.lee.playandroid.base.uistate.UiStatePageStateFlow
import com.lee.playandroid.base.uistate.applyData
import com.lee.playandroid.base.uistate.pageLaunch
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.system.model.api.ApiService
import com.lee.playandroid.system.ui.SystemContentListFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * 体系内容子页面contentList viewModel
 * @author jv.lee
 * @date 2021/11/10
 */
class SystemContentListViewModel(private val handle: SavedStateHandle) : ViewModel() {

    private val id: Long by lazy { handle[SystemContentListFragment.ARG_PARAMS_ID] ?: 0 }

    private val api = createApi<ApiService>()

    private val _contentListFlow: UiStatePageMutableStateFlow =
        MutableStateFlow(UiStatePage.Default(0))
    val contentListFlow: UiStatePageStateFlow = _contentListFlow

    fun dispatch(intent: SystemContentListViewIntent) {
        when (intent) {
            is SystemContentListViewIntent.RequestPage -> {
                requestContentList(intent.status)
            }
        }
    }

    private fun requestContentList(@LoadStatus status: Int) {
        viewModelScope.launch {
            _contentListFlow.pageLaunch(status, { page ->
                applyData { api.getContentDataAsync(page, id).checkData() }
            })
        }
    }
}

sealed class SystemContentListViewIntent {
    data class RequestPage(@LoadStatus val status: Int) : SystemContentListViewIntent()
}