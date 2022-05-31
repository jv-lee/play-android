package com.lee.playandroid.system.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.library.viewstate.*
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.system.model.api.ApiService
import com.lee.playandroid.system.ui.SystemContentListFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * 体系内容 子内容列表 ViewModel
 * @author jv.lee
 * @date 2021/11/10
 */
class SystemContentListViewModel(handle: SavedStateHandle) : ViewModel() {

    private val id: Long by lazy { handle[SystemContentListFragment.ARG_PARAMS_ID] ?: 0 }

    private val api = createApi<ApiService>()

    private val _contentListFlow: UiStatePageMutableStateFlow =
        MutableStateFlow(UiStatePage.Default(0))
    val contentListFlow: UiStatePageStateFlow = _contentListFlow

    fun dispatch(action: SystemContentListViewAction) {
        when (action) {
            is SystemContentListViewAction.RequestPage -> {
                requestContentList(action.status)
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

sealed class SystemContentListViewAction {
    data class RequestPage(@LoadStatus val status: Int) : SystemContentListViewAction()
}