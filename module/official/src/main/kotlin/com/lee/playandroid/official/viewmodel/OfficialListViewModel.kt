package com.lee.playandroid.official.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.cache.CacheManager
import com.lee.playandroid.base.extensions.getCache
import com.lee.playandroid.base.extensions.putPageCache
import com.lee.playandroid.base.viewstate.*
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.official.constants.Constants.CACHE_KEY_OFFICIAL_DATA
import com.lee.playandroid.official.model.api.ApiService
import com.lee.playandroid.official.ui.OfficialListFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * 公众号列表 ViewModel
 * @author jv.lee
 * @date 2021/11/8
 */
class OfficialListViewModel(handle: SavedStateHandle) : ViewModel() {

    private val id: Long by lazy { handle[OfficialListFragment.ARG_PARAMS_ID] ?: 0 }

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()

    private val _contentListFlow: UiStatePageMutableStateFlow =
        MutableStateFlow(UiStatePage.Default(1))
    val contentListFlow: UiStatePageStateFlow = _contentListFlow

    fun dispatch(action: OfficialListViewAction) {
        when (action) {
            is OfficialListViewAction.RequestPage -> {
                requestContentList(action.status)
            }
        }
    }

    private fun requestContentList(@LoadStatus status: Int) {
        viewModelScope.launch {
            _contentListFlow.pageLaunch(status, { page ->
                api.getOfficialDataAsync(id, page).checkData().also { newData ->
                    applyData(getValueData(), newData)
                }
            }, {
                cacheManager.getCache(CACHE_KEY_OFFICIAL_DATA + id)
            }, {
                cacheManager.putPageCache(CACHE_KEY_OFFICIAL_DATA + id, it)
            })
        }
    }

}

sealed class OfficialListViewAction {
    data class RequestPage(@LoadStatus val status: Int) : OfficialListViewAction()
}