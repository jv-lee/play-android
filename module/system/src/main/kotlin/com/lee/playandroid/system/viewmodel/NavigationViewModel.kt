package com.lee.playandroid.system.viewmodel

import androidx.lifecycle.MutableLiveData
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStateLiveData
import com.lee.library.mvvm.ui.UiStateMutableLiveData
import com.lee.library.mvvm.ui.stateFlow
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.system.constants.Constants
import com.lee.playandroid.system.model.repository.ApiRepository
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/11/12
 * @description
 */
class NavigationViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    private val _navigationLive = UiStateMutableLiveData()
    val navigationLive: UiStateLiveData = _navigationLive

    private val _selectTabLive = MutableLiveData(0)
    val selectTabLive = _selectTabLive

    fun requestNavigationData(@LoadStatus status: Int) {
        // 过滤navigation fragment 重复创建导致重复初始化请求
        if (status == LoadStatus.INIT && navigationLive.value != null) {
            return
        }

        launchIO {
            stateFlow({
                repository.api.getNavigationDataAsync().checkData()
                    .filter { it.articles.isNotEmpty() }
            }, {
                cacheManager.getCache(Constants.CACHE_KEY_NAVIGATION_CONTENT)
            }, {
                cacheManager.putCache(Constants.CACHE_KEY_NAVIGATION_CONTENT, it)
            }).collect {
                _navigationLive.postValue(it)
            }
        }
    }

    fun selectTabIndex(index: Int) {
        _selectTabLive.postValue(index)
    }

}