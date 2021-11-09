package com.lee.playandroid.project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.stateCacheFlow
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.library.utils.LogUtil
import com.lee.playandroid.project.constants.Constants
import com.lee.playandroid.project.model.repository.ApiRepository
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @data 2021/11/9
 * @description
 */
class ProjectViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    private val _tabsLive = MutableLiveData<UiState>()
    val tabsLive: LiveData<UiState> = _tabsLive

    fun requestTabs() {
        launchIO {
            stateCacheFlow({
                repository.api.getProjectTabsAsync().data
            }, {
                cacheManager.getCache(Constants.PROJECT_TAB_CACHE_KEY)
            }, {
                cacheManager.putCache(Constants.PROJECT_TAB_CACHE_KEY, it)
            }).collect {
                LogUtil.i("$it")
                _tabsLive.postValue(it)
            }
        }
    }

    init {
        requestTabs()
    }

}