package com.lee.playandroid.system.viewmodel

import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.viewstate.UiStateLiveData
import com.lee.library.viewstate.UiStateMutableLiveData
import com.lee.library.viewstate.stateCacheFlow
import com.lee.library.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.system.constants.Constants.CACHE_KEY_SYSTEM_CONTENT
import com.lee.playandroid.system.model.api.ApiService
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/11/10
 * @description 体系内容列表 ViewModel
 */
class SystemContentViewModel : CoroutineViewModel() {

    private val api = createApi<ApiService>()

    private val cacheManager = CacheManager.getDefault()

    private val _parentTabLive = UiStateMutableLiveData()
    val parentTabLive: UiStateLiveData = _parentTabLive

    fun requestParentTab() {
        launchIO {
            stateCacheFlow({
                api.getParentTabAsync().checkData().filter {
                    it.children.isNotEmpty()
                }
            }, {
                cacheManager.getCache(CACHE_KEY_SYSTEM_CONTENT)
            }, {
                cacheManager.putCache(CACHE_KEY_SYSTEM_CONTENT, it)
            }).collect {
                _parentTabLive.postValue(it)
            }
        }
    }

    init {
        requestParentTab()
    }

}