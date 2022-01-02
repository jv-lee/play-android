package com.lee.playandroid.home.viewmodel

import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putPageCache
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.home.bean.HomeContent
import com.lee.playandroid.home.constants.Constants
import com.lee.playandroid.home.model.entity.HomeCategory
import com.lee.playandroid.home.model.repository.ApiRepository
import com.lee.playandroid.library.common.entity.PageUiData
import com.lee.playandroid.library.common.extensions.checkData

/**
 * @author jv.lee
 * @date 2021/11/2
 * @description 首页viewModel
 */
class HomeViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    val contentListLive = UiStatePageLiveData(0)

    /**
     * 获取contentList列表
     */
    fun requestHomeData(@LoadStatus status: Int) {
        launchIO {
            contentListLive.pageLaunch(status, { page: Int ->
                buildHomePageData(page)
            }, {
                //缓存数据
                cacheManager.getCache(Constants.CACHE_KEY_HOME_CONTENT)
            }, {
                //存储缓存数据
                cacheManager.putPageCache(Constants.CACHE_KEY_HOME_CONTENT, it)
            })
        }
    }

    /**
     * 根据页码构建首页数据
     */
    private suspend fun UiStatePageLiveData.buildHomePageData(page: Int): PageUiData<HomeContent> {
        val dataList = mutableListOf<HomeContent>()

        //首页添加header数据
        if (page == getInitPage()) {
            val banner = repository.api.getBannerDataAsync().checkData()
            dataList.add(HomeContent(bannerList = banner))

            val category = HomeCategory.getHomeCategory()
            dataList.add(HomeContent(categoryList = category))
        }

        //获取网络item数据
        val textItemData = repository.api.getContentDataAsync(page).checkData().apply {
            data.forEach {
                dataList.add(HomeContent(content = it))
            }
        }

        //构建分页ui数据
        return PageUiData(
            textItemData.curPage,
            textItemData.total,
            dataList
        ).also { newData ->
            applyData(getValueData(), newData)
        }
    }

    init {
        requestHomeData(LoadStatus.INIT)
    }

}