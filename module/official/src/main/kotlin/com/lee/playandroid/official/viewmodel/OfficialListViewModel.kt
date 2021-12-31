package com.lee.playandroid.official.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putPageCache
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.official.constants.Constants
import com.lee.playandroid.official.model.repository.ApiRepository
import com.lee.playandroid.official.ui.OfficialListFragment

/**
 * @author jv.lee
 * @date 2021/11/8
 * @description 公众号列表 ViewModel
 */
class OfficialListViewModel(handle: SavedStateHandle) : CoroutineViewModel() {

    private val id: Long by lazy { handle[OfficialListFragment.ARG_PARAMS_ID] ?: 0 }

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    val contentListLive = UiStatePageLiveData(1)

    fun requestContentList(@LoadStatus status: Int) {
        launchIO {
            contentListLive.pageLaunch(status, { page ->
                applyData { repository.api.getOfficialDataAsync(id, page).checkData() }
            }, {
                cacheManager.getCache(Constants.CACHE_KEY_OFFICIAL_DATA + id)
            }, {
                cacheManager.putPageCache(Constants.CACHE_KEY_OFFICIAL_DATA + id, it)
            })
        }
    }

}