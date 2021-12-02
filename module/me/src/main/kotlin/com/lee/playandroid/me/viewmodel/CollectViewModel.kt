package com.lee.playandroid.me.viewmodel

import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.me.model.repository.ApiRepository

/**
 * @author jv.lee
 * @date 2021/12/2
 * @description
 */
class CollectViewModel : CoroutineViewModel() {

    private val repository = ApiRepository()

    val collectLive = UiStatePageLiveData(0)

    fun requestCollect(@LoadStatus status: Int) {
        launchIO {
            collectLive.apply {
                pageLaunch(status, { page ->
                    repository.api.getCollectListAsync(page).checkData().also { newData ->
                        applyData(getValueData<PageData<Content>>()?.data, newData.data)
                    }
                })
            }
        }
    }

    init {
        requestCollect(LoadStatus.INIT)
    }

}