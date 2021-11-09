package com.lee.playandroid.project.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.pioneer.library.common.entity.Content
import com.lee.pioneer.library.common.entity.PageData
import com.lee.playandroid.project.model.repository.ApiRepository
import com.lee.playandroid.project.ui.ProjectListFragment
import kotlinx.coroutines.delay

/**
 * @author jv.lee
 * @data 2021/11/9
 * @description
 */
class ProjectListViewModel(handle: SavedStateHandle) : CoroutineViewModel() {

    private val id: Long by lazy { handle[ProjectListFragment.ARG_PARAMS_ID] ?: 0 }

    private val repository = ApiRepository()

    val contentListLive = UiStatePageLiveData(initPage = 1)

    fun requestContentList(@LoadStatus status: Int) {
        launchIO {
            delay(100)
            contentListLive.apply {
                pageLaunch(status, { page ->
                    repository.api.getProjectDataAsync(page, id).data.also { newData ->
                        applyData(getValueData<PageData<Content>>()?.data, newData.data)
                    }
                })
            }
        }
    }

}