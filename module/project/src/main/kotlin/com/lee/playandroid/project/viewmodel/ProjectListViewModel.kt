package com.lee.playandroid.project.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putPageCache
import com.lee.library.viewmodel.CoroutineViewModel
import com.lee.library.viewstate.*
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.project.constants.Constants
import com.lee.playandroid.project.model.api.ApiService
import com.lee.playandroid.project.ui.ProjectListFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2021/11/9
 * @description 项目列表ViewModel
 */
class ProjectListViewModel(handle: SavedStateHandle) : CoroutineViewModel() {

    private val id: Long by lazy { handle[ProjectListFragment.ARG_PARAMS_ID] ?: 0 }

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()

    private val _contentListFlow: UiStatePageMutableStateFlow =
        MutableStateFlow(UiStatePage.Default(0))
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