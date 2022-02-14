package com.lee.playandroid.project.viewmodel

import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.mvvm.ui.UiStateLiveData
import com.lee.library.mvvm.ui.UiStateMutableLiveData
import com.lee.library.mvvm.ui.stateFlow
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.project.constants.Constants
import com.lee.playandroid.project.model.repository.ApiRepository
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/11/9
 * @description 项目Tab ViewModel
 */
class ProjectViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    private val _tabsLive = UiStateMutableLiveData()
    val tabsLive: UiStateLiveData = _tabsLive

    fun requestTabs() {
        launchIO {
            stateFlow({
                repository.api.getProjectTabsAsync().checkData()
            }, {
                cacheManager.getCache(Constants.CACHE_KEY_PROJECT_TAB)
            }, {
                cacheManager.putCache(Constants.CACHE_KEY_PROJECT_TAB, it)
            }).collect {
                _tabsLive.postValue(it)
            }
        }
    }

    init {
        requestTabs()
    }

}