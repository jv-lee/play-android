package com.lee.playandroid.official.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.library.utils.LogUtil
import com.lee.pioneer.library.common.entity.Content
import com.lee.pioneer.library.common.entity.PageData
import com.lee.playandroid.official.constants.Constants
import com.lee.playandroid.official.model.repository.ApiRepository
import com.lee.playandroid.official.ui.OfficialListFragment
import kotlinx.coroutines.delay

/**
 * @author jv.lee
 * @data 2021/11/8
 * @description
 */
class OfficialListViewModel(handle: SavedStateHandle) : CoroutineViewModel() {

    private val id: Long by lazy { handle[OfficialListFragment.ARG_PARAMS_ID] ?: 0 }

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    val contentListLive = UiStatePageLiveData(initPage = 1)

    fun requestContentList(@LoadStatus status: Int) {
        launchIO {
            contentListLive.apply {
                pageLaunch(status, { page ->
                    repository.api.getOfficialDataAsync(id, page).data.also { newData ->
                        applyData(getValueData<PageData<Content>>()?.data, newData.data)
                    }
                }, {
                    cacheManager.getCache(Constants.OFFICIAL_DATA_CACHE_KEY + id)
                }, {
                    cacheManager.putCache(Constants.OFFICIAL_DATA_CACHE_KEY + id, it)
                })
            }
        }
    }

}