package com.lee.playandroid.system.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.stateCacheFlow
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.system.constants.Constants
import com.lee.playandroid.system.model.repository.ApiRepository
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @data 2021/11/12
 * @description
 */
class NavigationViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    private val _navigationLive = MutableLiveData<UiState>()
    val navigationLive: LiveData<UiState> = _navigationLive

    private val _selectTabLive = MutableLiveData(0)
    val selectTabLive = _selectTabLive

    fun requestNavigationData() {
        launchIO {
            stateCacheFlow({
                repository.api.getNavigationDataAsync().data
                    .filter {
                        //navigation框架url规则判断存在bug 过滤存在错误url的板块
                        it.name != "个人博客" && it.name != "优秀的博客" && it.articles.isNotEmpty()
                    }
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