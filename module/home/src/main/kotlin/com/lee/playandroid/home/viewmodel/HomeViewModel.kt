package com.lee.playandroid.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.ui.stateCacheFlow
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.pioneer.library.common.entity.Banner
import com.lee.pioneer.library.common.entity.Content
import com.lee.pioneer.library.common.entity.PageData
import com.lee.playandroid.home.constants.Constants.HOME_BANNER_CACHE_KEY
import com.lee.playandroid.home.constants.Constants.HOME_CONTENT_CACHE_KEY
import com.lee.playandroid.home.model.repository.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @data 2021/11/2
 * @description 首页viewModel
 */
class HomeViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    private val _bannerLive = MutableLiveData<UiState>()
    val bannerLive: LiveData<UiState> = _bannerLive

    val contentListLive = UiStatePageLiveData(initPage = 0)

    /**
     * 网络获取banner数据及使用本地缓存
     */
    fun requestBanner() {
        viewModelScope.launch {
            stateCacheFlow({
                repository.api.getBannerDataAsync().data
            }, {
                cacheManager.getCache<MutableList<Banner>>(HOME_BANNER_CACHE_KEY)
            }, {
                cacheManager.putCache(HOME_BANNER_CACHE_KEY, it)
            })
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    _bannerLive.postValue(it)
                }
        }
    }

    /**
     * 获取contentList列表
     */
    fun loadListData(@LoadStatus status: Int) {
        launchMain {
            contentListLive.pageLaunch(status, { page: Int ->
                //网络数据
                repository.api.getContentDataAsync(page).data.also { response ->
                    //填充历史数据 让activity在重建时可以从liveData中获取到完整数据 首页无需填充原始数据(会造成数据重复)
                    contentListLive.applyData(
                        contentListLive.getValueData<PageData<Content>>()?.data,
                        response.data
                    )
                }
            }, {
                //缓存数据
                cacheManager.getCache<PageData<Content>>(HOME_CONTENT_CACHE_KEY)
            }, {
                //存储缓存数据
                cacheManager.putCache(HOME_CONTENT_CACHE_KEY, it)
            })
        }
    }

    init {
        requestBanner()
        loadListData(LoadStatus.INIT)
    }

}