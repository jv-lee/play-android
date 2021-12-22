package com.lee.playandroid.me.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.library.base.BaseApplication
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.extensions.putPageCache
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.ui.stateFlow
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.me.R
import com.lee.playandroid.me.constants.Constants
import com.lee.playandroid.me.model.repository.ApiRepository
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/12/2
 * @description 收藏页面ViewModel
 */
class CollectViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    private val _unCollectLive = MutableLiveData<UiState>()
    val unCollectLive: LiveData<UiState> = _unCollectLive

    val collectLive = UiStatePageLiveData(0)

    fun requestCollect(@LoadStatus status: Int) {
        launchIO {
            collectLive.apply {
                pageLaunch(status, { page ->
                    repository.api.getCollectListAsync(page).checkData().also { newData ->
                        applyData(getValueData(), newData)
                    }
                }, {
                    cacheManager.getCache<PageData<Content>>(Constants.CACHE_KEY_COLLECT)
                }, {
                    cacheManager.putPageCache(Constants.CACHE_KEY_COLLECT, it)
                })
            }
        }
    }

    fun requestUnCollect(content: Content) {
        launchIO {
            stateFlow {
                val response = repository.api.postUnCollectAsync(content.id, content.originId)
                if (response.errorCode == ApiConstants.REQUEST_OK) {
                    updateCacheData(content)
                    BaseApplication.getContext().getString(R.string.collect_remove_item_success)
                } else {
                    response.errorMsg
                }
            }.collect {
                _unCollectLive.postValue(it)
            }
        }
    }

    /**
     * 更新首页缓存
     */
    private fun updateCacheData(content: Content) {
        val data = collectLive.getCacheValueData<PageData<Content>>() ?: return
        if (data.data.contains(content)) {
            data.data.remove(content)
        }
        cacheManager.putCache(Constants.CACHE_KEY_COLLECT, data)
    }

    init {
        requestCollect(LoadStatus.INIT)
    }

}