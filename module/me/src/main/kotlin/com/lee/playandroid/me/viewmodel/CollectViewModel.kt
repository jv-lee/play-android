package com.lee.playandroid.me.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.lee.playandroid.me.constants.Constants
import com.lee.playandroid.me.model.repository.ApiRepository
import kotlinx.coroutines.flow.collect
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author jv.lee
 * @date 2021/12/2
 * @description 收藏页面ViewModel
 */
class CollectViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    private val deleteLock = AtomicBoolean(false)

    private val _unCollectLive = MutableLiveData<UiState>()
    val unCollectLive: LiveData<UiState> = _unCollectLive

    val collectLive = UiStatePageLiveData(0)

    /**
     * 请求收藏内容
     * @param status 分页状态
     */
    fun requestCollect(@LoadStatus status: Int) {
        launchIO {
            collectLive.apply {
                pageLaunch(status, { page ->
                    applyData { repository.api.getCollectListAsync(page).checkData() }
                }, {
                    cacheManager.getCache<PageData<Content>>(Constants.CACHE_KEY_COLLECT)
                }, {
                    cacheManager.putPageCache(Constants.CACHE_KEY_COLLECT, it)
                })
            }
        }
    }

    /**
     * 请求移除收藏内容
     * @param position 收藏内容下标
     */
    fun requestUnCollect(position: Int) {
        if (deleteLock.get()) return
        deleteLock.set(true)

        launchIO {
            stateFlow {
                val data = collectLive.getValueData<PageData<Content>>()!!
                val item = data.data[position]

                val response = repository.api.postUnCollectAsync(item.id, item.originId)
                if (response.errorCode == ApiConstants.REQUEST_OK) {
                    removeCacheItem(item)
                    position
                } else {
                    throw RuntimeException(response.errorMsg)
                }
            }.collect {
                deleteLock.set(false)
                _unCollectLive.postValue(it)
            }
        }
    }

    /**
     * 更新首页缓存
     * @param content 被更新的数据实体
     */
    private fun removeCacheItem(content: Content) {
        // 内存移除
        collectLive.getValueData<PageData<Content>>()?.apply {
            this.data.remove(content)
        }

        // 缓存移除
        cacheManager.getCache<PageData<Content>>(Constants.CACHE_KEY_COLLECT)?.apply {
            if (data.remove(content)) {
                cacheManager.putCache(Constants.CACHE_KEY_COLLECT, this)
            }
        }
    }

    init {
        requestCollect(LoadStatus.INIT)
    }

}