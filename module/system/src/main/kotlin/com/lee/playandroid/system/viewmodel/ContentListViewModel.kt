package com.lee.playandroid.system.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.lee.library.mvvm.annotation.LoadStatus
import com.lee.library.mvvm.ui.*
import com.lee.library.mvvm.vm.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.system.model.api.ApiService
import com.lee.playandroid.system.ui.ContentListFragment

/**
 * @author jv.lee
 * @date 2021/11/10
 * @description 体系内容 子内容列表 ViewModel
 */
class ContentListViewModel(handle: SavedStateHandle) : CoroutineViewModel() {

    private val id: Long by lazy { handle[ContentListFragment.ARG_PARAMS_ID] ?: 0 }

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