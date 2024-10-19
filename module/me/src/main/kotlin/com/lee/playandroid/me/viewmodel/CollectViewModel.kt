package com.lee.playandroid.me.viewmodel

import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.base.ApplicationExtensions.app
import com.lee.playandroid.base.cache.CacheManager
import com.lee.playandroid.base.extensions.getCache
import com.lee.playandroid.base.extensions.lowestTime
import com.lee.playandroid.base.extensions.putCache
import com.lee.playandroid.base.extensions.putPageCache
import com.lee.playandroid.base.uistate.LoadStatus
import com.lee.playandroid.base.uistate.UiStatePage
import com.lee.playandroid.base.uistate.UiStatePageMutableStateFlow
import com.lee.playandroid.base.uistate.UiStatePageStateFlow
import com.lee.playandroid.base.uistate.applyData
import com.lee.playandroid.base.uistate.getValueData
import com.lee.playandroid.base.uistate.pageLaunch
import com.lee.playandroid.base.utils.NetworkUtil
import com.lee.playandroid.base.viewmodel.BaseMVIViewModel
import com.lee.playandroid.base.viewmodel.IViewEvent
import com.lee.playandroid.base.viewmodel.IViewIntent
import com.lee.playandroid.base.viewmodel.IViewState
import com.lee.playandroid.common.constants.ApiConstants
import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.entity.PageData
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.me.R
import com.lee.playandroid.common.R as CR
import com.lee.playandroid.me.constants.Constants.CACHE_KEY_COLLECT
import com.lee.playandroid.me.model.api.ApiService
import com.lee.playandroid.service.AccountService
import com.lee.playandroid.service.hepler.ModuleService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 收藏页面ViewModel
 * @author jv.lee
 * @date 2021/12/2
 */
class CollectViewModel : BaseMVIViewModel<CollectViewState, CollectViewEvent, CollectViewIntent>() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()
    private val accountService: AccountService = ModuleService.find()

    private val cacheKey = CACHE_KEY_COLLECT.plus(accountService.getUserId())
    private val deleteLock = AtomicBoolean(false)

    private val _collectFlow: UiStatePageMutableStateFlow = MutableStateFlow(UiStatePage.Default(0))
    val collectFlow: UiStatePageStateFlow = _collectFlow

    init {
        dispatch(CollectViewIntent.RequestPage(LoadStatus.INIT))
    }

    override fun initViewState() = CollectViewState()

    override fun dispatch(intent: CollectViewIntent) {
        when (intent) {
            is CollectViewIntent.RequestPage -> {
                requestCollect(intent.status)
            }

            is CollectViewIntent.UnCollect -> {
                requestUnCollect(intent.position)
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
                    check(NetworkUtil.isNetworkConnected(app)) {
                        app.getString(CR.string.network_not_access)
                    }

                    val data = _collectFlow.getValueData<PageData<Content>>()!!
                    val item = data.data[position]

                    val response = api.postUnCollectAsync(item.id, item.originId)
                    if (response.errorCode == ApiConstants.REQUEST_OK) {
                        removeCacheItem(item)
                        emit(position)
                    } else {
                        throw RuntimeException(response.errorMsg)
                    }
                }.onStart {
                    _viewEvents.send(CollectViewEvent.ResetSlidingState)
                    _viewStates.update { it.copy(isLoading = true) }
                }.catch { error ->
                    _viewEvents.send(CollectViewEvent.UnCollectFailed(error = error))
                }.onCompletion {
                    deleteLock.set(false)
                    _viewStates.update { it.copy(isLoading = false) }
                }.lowestTime().collect {
                    _viewEvents.send(CollectViewEvent.UnCollectSuccess(position))
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

data class CollectViewState(
    val isLoading: Boolean = false
) : IViewState

sealed class CollectViewEvent : IViewEvent {
    object ResetSlidingState : CollectViewEvent()
    data class UnCollectSuccess(val position: Int) : CollectViewEvent()
    data class UnCollectFailed(val error: Throwable) : CollectViewEvent()
}

sealed class CollectViewIntent : IViewIntent {
    data class RequestPage(@LoadStatus val status: Int) : CollectViewIntent()
    data class UnCollect(val position: Int) : CollectViewIntent()
}