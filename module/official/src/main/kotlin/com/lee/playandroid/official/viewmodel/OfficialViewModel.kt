package com.lee.playandroid.official.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.stateCacheFlow
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.official.constants.Constants
import com.lee.playandroid.official.model.repository.ApiRepository
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @data 2021/11/8
 * @description
 */
class OfficialViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    private val _tabsLive = MutableLiveData<UiState>()
    val tabsLive: LiveData<UiState> = _tabsLive

    fun requestTabs() {
        launchIO {
            stateCacheFlow({
                repository.api.getOfficialTabsAsync().data
            }, {
                cacheManager.getCache(Constants.OFFICIAL_TAB_CACHE_KEY)
            }, {
                cacheManager.putCache(Constants.OFFICIAL_TAB_CACHE_KEY, it)
            }).collect {
                _tabsLive.postValue(it)
            }
        }
    }

}