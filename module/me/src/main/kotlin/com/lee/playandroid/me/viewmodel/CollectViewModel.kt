package com.lee.playandroid.me.viewmodel

import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.extensions.putPageCache
import com.lee.library.viewmodel.CoroutineViewModel
import com.lee.library.viewstate.*
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService
import com.lee.playandroid.me.constants.Constants.CACHE_KEY_COLLECT
import com.lee.playandroid.me.model.api.ApiService
import kotlinx.coroutines.flow.collect
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author jv.lee
 * @date 2021/12/2
 * @description 收藏页面ViewModel
 */
class CollectViewModel : CoroutineViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()
    private val accountService: AccountService = ModuleService.find()

    private val cacheKey = CACHE_KEY_COLLECT.plus(accountService.getUserId())
    private val deleteLock = AtomicBoolean(false)

    private val _unCollectLive = UiStateMutableLiveData()
    val unCollectLive: UiStateLiveData = _unCollectLive

    private val _collectLive = UiStatePageMutableLiveData(UiStatePage.Default(0))
    val collectLive: UiStatePageLiveData = _collectLive

    /**
     * 请求收藏内容
     * @param status 分页状态
     */
    fun requestCollect(@LoadStatus status: Int) {
        launchIO {
            _collectLive.pageLaunch(status, { page ->
                applyData { api.getCollectListAsync(page).checkData() }
            }, {
                cacheManager.getCache(cacheKey)
            }, {
                cacheManager.putPageCache(cacheKey, it)
            })
        }
    }

    /**
     * 请求移除收藏内容
     * @param position 收藏内容下标
     */
    fun requestUnCollect(position: Int) {
        if (deleteLock.compareAndSet(false, true)) {
            launchIO {
                stateFlow {
                    val data = collectLive.getValueData<PageData<Content>>()!!
                    val item = data.data[position]

                    val response = api.postUnCollectAsync(item.id, item.originId)
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
        cacheManager.getCache<PageData<Content>>(cacheKey)?.apply {
            if (data.remove(content)) {
                cacheManager.putCache(cacheKey, this)
            }
        }
    }

    init {
        requestCollect(LoadStatus.INIT)
    }

}