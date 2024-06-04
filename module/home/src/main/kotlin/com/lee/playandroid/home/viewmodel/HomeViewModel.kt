package com.lee.playandroid.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.cache.CacheManager
import com.lee.playandroid.base.extensions.getCache
import com.lee.playandroid.base.extensions.putPageCache
import com.lee.playandroid.base.uistate.*
import com.lee.playandroid.base.viewmodel.IViewIntent
import com.lee.playandroid.common.entity.PageUiData
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.home.bean.HomeContent
import com.lee.playandroid.home.constants.Constants
import com.lee.playandroid.home.model.api.ApiService
import com.lee.playandroid.home.model.entity.HomeCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * 首页viewModel
 * @author jv.lee
 * @date 2021/11/2
 */
class HomeViewModel : ViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()

    private val _contentListFlow: UiStatePageMutableStateFlow =
        MutableStateFlow(UiStatePage.Default(0))
    val contentListFlow: UiStatePageStateFlow = _contentListFlow

    init {
        dispatch(HomeViewIntent.RequestPage(LoadStatus.INIT))
    }

    fun dispatch(intent: HomeViewIntent) {
        when (intent) {
            is HomeViewIntent.RequestPage -> {
                requestHomeData(intent.status)
            }
        }
    }

    /**
     * 获取contentList列表
     */
    private fun requestHomeData(@LoadStatus status: Int) {
        viewModelScope.launch {
            _contentListFlow.pageLaunch(status, { page: Int ->
                buildHomePageData(page)
            }, {
                // 缓存数据
                cacheManager.getCache(Constants.CACHE_KEY_HOME_CONTENT)
            }, {
                // 存储缓存数据
                cacheManager.putPageCache(Constants.CACHE_KEY_HOME_CONTENT, it)
            })
        }
    }

    /**
     * 根据页码构建首页数据
     */
    private suspend fun MutableStateFlow<UiStatePage>.buildHomePageData(page: Int):
        PageUiData<HomeContent> {
        val dataList = mutableListOf<HomeContent>()

        // 首页添加header数据
        if (page == requestFirstPage) {
            val banner = api.getBannerDataAsync().checkData()
            dataList.add(HomeContent(bannerList = banner))

            val category = HomeCategory.getHomeCategory()
            dataList.add(HomeContent(categoryList = category))
        }

        // 获取网络item数据
        val textItemData = api.getContentDataAsync(page).checkData().apply {
            data.forEach { dataList.add(HomeContent(content = it)) }
        }

        // 构建分页ui数据
        return PageUiData(
            textItemData.curPage,
            textItemData.total,
            dataList
        ).also { newData ->
            applyData(getValueData(), newData)
        }
    }
}

sealed class HomeViewIntent : IViewIntent {
    data class RequestPage(@LoadStatus val status: Int) : HomeViewIntent()
}