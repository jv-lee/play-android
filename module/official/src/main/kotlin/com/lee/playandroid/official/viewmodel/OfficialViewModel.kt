package com.lee.playandroid.official.viewmodel

import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.mvvm.ui.UiStateLiveData
import com.lee.library.mvvm.ui.UiStateMutableLiveData
import com.lee.library.mvvm.ui.stateCacheFlow
import com.lee.library.mvvm.vm.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.official.constants.Constants
import com.lee.playandroid.official.model.api.ApiService
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/11/8
 * @description 公众号Tab ViewModel
 */
class OfficialViewModel : CoroutineViewModel() {

    private val api = createApi<ApiService>()

    private val cacheManager = CacheManager.getDefault()

    private val _tabsLive = UiStateMutableLiveData()
    val tabsLive: UiStateLiveData = _tabsLive

    fun requestTabs() {
        launchIO {
            stateCacheFlow({
                api.getOfficialTabsAsync().checkData()
            }, {
                cacheManager.getCache(Constants.CACHE_KEY_OFFICIAL_TAB)
            }, {
                cacheManager.putCache(Constants.CACHE_KEY_OFFICIAL_TAB, it)
            }).collect {
                _tabsLive.postValue(it)
            }
        }
    }

    init {
        requestTabs()
    }

}