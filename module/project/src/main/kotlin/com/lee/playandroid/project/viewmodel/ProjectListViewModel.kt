package com.lee.playandroid.project.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.cache.CacheManager
import com.lee.playandroid.base.extensions.getCache
import com.lee.playandroid.base.extensions.putPageCache
import com.lee.playandroid.base.viewstate.*
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.project.constants.Constants
import com.lee.playandroid.project.model.api.ApiService
import com.lee.playandroid.project.ui.ProjectListFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * 项目列表ViewModel
 * @author jv.lee
 * @date 2021/11/9
 */
class ProjectListViewModel(handle: SavedStateHandle) : ViewModel() {

    private val id: Long by lazy { handle[ProjectListFragment.ARG_PARAMS_ID] ?: 0 }

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()

    private val _contentListFlow: UiStatePageMutableStateFlow =
        MutableStateFlow(UiStatePage.Default(1))
    val contentListFlow: UiStatePageStateFlow = _contentListFlow

    fun dispatch(action: ProjectListViewAction) {
        when (action) {
            is ProjectListViewAction.RequestPage -> {
                requestContentList(action.status)
            }
        }
    }

    private fun requestContentList(@LoadStatus status: Int) {
        viewModelScope.launch {
            _contentListFlow.pageLaunch(status, { page ->
                api.getProjectDataAsync(page, id).checkData().also { newData ->
                    applyData(getValueData(), newData)
                }
            }, {
                cacheManager.getCache(Constants.CACHE_KEY_PROJECT_DATA + id)
            }, {
                cacheManager.putPageCache(Constants.CACHE_KEY_PROJECT_DATA + id, it)
            })
        }
    }

}

sealed class ProjectListViewAction {
    data class RequestPage(@LoadStatus val status: Int) : ProjectListViewAction()
}