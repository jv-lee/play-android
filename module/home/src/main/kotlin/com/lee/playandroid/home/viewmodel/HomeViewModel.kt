package com.lee.playandroid.home.viewmodel

import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.pioneer.library.common.entity.PageUiData
import com.lee.playandroid.home.bean.HomeContent
import com.lee.playandroid.home.constants.Constants
import com.lee.playandroid.home.helper.CategoryHelper
import com.lee.playandroid.home.model.repository.ApiRepository

/**
 * @author jv.lee
 * @data 2021/11/2
 * @description 首页viewModel
 */
class HomeViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    val contentListLive = UiStatePageLiveData()

    /**
     * 获取contentList列表
     */
    fun loadListData(@LoadStatus status: Int) {
        launchMain {
            launchIO {
                contentListLive.pageLaunch(status, { page: Int ->
                    val dataList = mutableListOf<HomeContent>()

                    //首页添加header数据
                    if (page == contentListLive.getInitPage()) {
                        val banner = repository.api.getBannerDataAsync().data
                        dataList.add(HomeContent(bannerList = banner))

                        val category = CategoryHelper.getHomeCategory()
                        dataList.add(HomeContent(categoryList = category))
                    }

                    //获取网络item数据
                    val textItemData = repository.api.getContentDataAsync(page).data.apply {
                        data.forEach {
                            dataList.add(HomeContent(content = it))
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
    }

    init {
        loadListData(LoadStatus.INIT)
    }

}