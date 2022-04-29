package com.lee.playandroid.me.viewmodel

import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    private val _collectFlow: UiStatePageMutableStateFlow = MutableStateFlow(UiStatePage.Default(0))
    val collectFlow: UiStatePageStateFlow = _collectFlow

    private val _viewEvents = Channel<CollectViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    init {
        dispatch(CollectViewAction.RequestPage(LoadStatus.INIT))
    }

    fun dispatch(action: CollectViewAction) {
        when (action) {
            is CollectViewAction.RequestPage -> {
                requestCollect(action.status)
            }
            is CollectViewAction.UnCollect -> {
                requestUnCollect(action.position)
            }
        }
    }

    /**
     * 请求收藏内容
     * @param status 分页状态
     */
    private fun requestCollect(@LoadStatus status: Int) {
        viewModelScope.launch {
            _collectFlow.pageLaunch(status, { page ->
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
    private fun requestUnCollect(position: Int) {
        if (deleteLock.compareAndSet(false, true)) {
            viewModelScope.launch {
                flow {
                    val data = _collectFlow.getValueData<PageData<Content>>()!!
                    val item = data.data[position]

                    val response = api.postUnCollectAsync(item.id, item.originId)
                    if (response.errorCode == ApiConstants.REQUEST_OK) {
                        removeCacheItem(item)
                        emit(position)
                    } else {
                        throw RuntimeException(response.errorMsg)
                    }
                }.catch { error ->
                    deleteLock.set(false)
                    _viewEvents.send(CollectViewEvent.UnCollectFailed(error = error))
                }.collect {
                    deleteLock.set(false)
                    _viewEvents.send(CollectViewEvent.UnCollectSuccess)
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
        _collectFlow.getValueData<PageData<Content>>()?.apply {
            this.data.remove(content)
        }

        // 缓存移除
        cacheManager.getCache<PageData<Content>>(cacheKey)?.apply {
            if (data.remove(content)) {
                cacheManager.putCache(cacheKey, this)
            }
        }
    }

}

sealed class CollectViewAction {
    data class RequestPage(@LoadStatus val status: Int) : CollectViewAction()
    data class UnCollect(val position: Int) : CollectViewAction()
}

sealed class CollectViewEvent {
    object UnCollectSuccess : CollectViewEvent()
    data class UnCollectFailed(val error: Throwable) : CollectViewEvent()
}