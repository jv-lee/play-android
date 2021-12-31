package com.lee.playandroid.project.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putPageCache
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.project.constants.Constants
import com.lee.playandroid.project.model.repository.ApiRepository
import com.lee.playandroid.project.ui.ProjectListFragment

/**
 * @author jv.lee
 * @date 2021/11/9
 * @description 项目列表ViewModel
 */
class ProjectListViewModel(handle: SavedStateHandle) : CoroutineViewModel() {

    private val id: Long by lazy { handle[ProjectListFragment.ARG_PARAMS_ID] ?: 0 }

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    val contentListLive = UiStatePageLiveData(1)

    fun requestContentList(@LoadStatus status: Int) {
        launchIO {
            contentListLive.pageLaunch(status, { page ->
                repository.api.getProjectDataAsync(page, id).checkData().also { newData ->
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