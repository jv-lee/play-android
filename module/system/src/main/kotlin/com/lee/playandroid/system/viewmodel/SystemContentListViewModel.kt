package com.lee.playandroid.system.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.lee.library.viewmodel.CoroutineViewModel
import com.lee.library.viewstate.*
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.system.model.api.ApiService
import com.lee.playandroid.system.ui.SystemContentListFragment

/**
 * @author jv.lee
 * @date 2021/11/10
 * @description 体系内容 子内容列表 ViewModel
 */
class SystemContentListViewModel(handle: SavedStateHandle) : CoroutineViewModel() {

    private val id: Long by lazy { handle[SystemContentListFragment.ARG_PARAMS_ID] ?: 0 }

    private val api = createApi<ApiService>()

    private val _contentListLive = UiStatePageMutableLiveData(UiStatePage.Default(0))
    val contentListLive: UiStatePageLiveData = _contentListLive

    fun requestContentList(@LoadStatus status: Int) {
        launchIO {
            _contentListLive.pageLaunch(status, { page ->
                applyData { api.getContentDataAsync(page, id).checkData() }
            })
        }
    }

}