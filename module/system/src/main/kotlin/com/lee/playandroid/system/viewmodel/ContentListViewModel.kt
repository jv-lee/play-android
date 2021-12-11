package com.lee.playandroid.system.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.system.model.repository.ApiRepository
import com.lee.playandroid.system.ui.ContentListFragment

/**
 * @author jv.lee
 * @date 2021/11/10
 * @description 体系内容 子内容列表 ViewModel
 */
class ContentListViewModel(handle: SavedStateHandle) : CoroutineViewModel() {

    private val id: Long by lazy { handle[ContentListFragment.ARG_PARAMS_ID] ?: 0 }

    private val repository = ApiRepository()

    val contentListLive = UiStatePageLiveData()

    fun requestContentList(@LoadStatus status: Int) {
        launchIO {
            contentListLive.apply {
                pageLaunch(status, { page ->
                    repository.api.getContentDataAsync(page, id).checkData().also { newData ->
                        applyData(getValueData<PageData<Content>>(), newData)
                    }
                })
            }
        }
    }

}