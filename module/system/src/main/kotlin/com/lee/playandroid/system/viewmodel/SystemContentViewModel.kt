package com.lee.playandroid.system.viewmodel

import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.mvvm.ui.UiStateLiveData
import com.lee.library.mvvm.ui.UiStateMutableLiveData
import com.lee.library.mvvm.ui.stateCacheFlow
import com.lee.library.mvvm.base.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.system.constants.Constants.CACHE_KEY_SYSTEM_CONTENT
import com.lee.playandroid.system.model.repository.ApiRepository
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/11/10
 * @description 体系内容列表 ViewModel
 */
class SystemContentViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    private val _parentTabLive = UiStateMutableLiveData()
    val parentTabLive: UiStateLiveData = _parentTabLive

    fun requestParentTab() {
        launchIO {
            stateCacheFlow({
                repository.api.getParentTabAsync().checkData().filter {
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