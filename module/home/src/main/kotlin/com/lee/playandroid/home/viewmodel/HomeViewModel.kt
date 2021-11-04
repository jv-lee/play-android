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
import com.lee.pioneer.library.common.entity.PageUiData
import com.lee.playandroid.home.bean.HomeContent
import com.lee.playandroid.home.constants.Constants
import com.lee.playandroid.home.constants.Constants.HOME_BANNER_CACHE_KEY
import com.lee.playandroid.home.helper.CategoryHelper
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
                val dataList = mutableListOf<HomeContent>()

                //首页添加分类数据
                if (page == 0) {
                    val category = CategoryHelper.getHomeCategory()
                    dataList.add(HomeContent(categoryList = category))
                }

                //获取网络item数据
                val textItemData = repository.api.getContentDataAsync(page).data.apply {
                    data.forEach { content ->
                        dataList.add(HomeContent(content))
                    }
                }

                //构建分页ui数据
                PageUiData(textItemData.curPage, textItemData.total, dataList).also { newData ->
                    contentListLive.applyData(
                        contentListLive.getValueData<PageUiData<HomeContent>>()?.data,
                        newData.data
                    )
                }
            }, {
                //缓存数据
                cacheManager.getCache<PageUiData<HomeContent>>(Constants.HOME_CONTENT_CACHE_KEY)
            }, {
                //存储缓存数据
                cacheManager.putCache(Constants.HOME_CONTENT_CACHE_KEY, it)
            })
        }
    }

    init {
        requestBanner()
        loadListData(LoadStatus.INIT)
    }

}