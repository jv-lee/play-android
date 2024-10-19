package com.lee.playandroid.square.viewmodel

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
import com.lee.playandroid.service.AccountService
import com.lee.playandroid.service.hepler.ModuleService
import com.lee.playandroid.square.R
import com.lee.playandroid.common.R as CR
import com.lee.playandroid.square.constants.Constants.CACHE_KEY_MY_SHARE_CONTENT
import com.lee.playandroid.square.model.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 我的分享内容页viewModel
 * @author jv.lee
 * @date 2021/12/16
 */
class MyShareViewModel : BaseMVIViewModel<MyShareViewState, MyShareViewEvent, MyShareViewIntent>() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()
    private val accountService: AccountService = ModuleService.find()

    private val cacheKey = CACHE_KEY_MY_SHARE_CONTENT.plus(accountService.getUserId())
    private val deleteLock = AtomicBoolean(false)

    private val _myShareFlow: UiStatePageMutableStateFlow = MutableStateFlow(UiStatePage.Default(1))
    val myShareFlow: StateFlow<UiStatePage> = _myShareFlow

    init {
        dispatch(MyShareViewIntent.RequestPage(LoadStatus.INIT))
    }

    override fun initViewState() = MyShareViewState()

    override fun dispatch(intent: MyShareViewIntent) {
        when (intent) {
            is MyShareViewIntent.RequestPage -> {
                requestMyShareData(intent.status)
            }
            is MyShareViewIntent.DeleteShare -> {
                requestDeleteShare(intent.position)
            }
        }
    }

    private fun requestMyShareData(@LoadStatus status: Int) {
        viewModelScope.launch {
            _myShareFlow.pageLaunch(status, { page ->
                api.getMyShareDataSync(page).checkData().shareArticles.also { newData ->
                    applyData(getValueData(), newData)
                }
            }, {
                cacheManager.getCache(cacheKey)
            }, {
                cacheManager.putPageCache(cacheKey, it)
            })
        }
    }

    private fun requestDeleteShare(position: Int) {
        if (deleteLock.compareAndSet(false, true)) {
            viewModelScope.launch {
                flow {
                    check(NetworkUtil.isNetworkConnected(app)) {
                        app.getString(CR.string.network_not_access)
                    }

                    val data = _myShareFlow.getValueData<PageData<Content>>()!!
                    val item = data.data[position]

                    val response = api.postDeleteShareAsync(item.id)
                    if (response.errorCode == ApiConstants.REQUEST_OK) {
                        removeCacheItem(item)
                        emit(position)
                    } else {
                        throw RuntimeException(response.errorMsg)
                    }
                }.onStart {
                    _viewEvents.send(MyShareViewEvent.ResetSlidingState)
                    _viewStates.update { it.copy(isLoading = true) }
                }.catch { error ->
                    _viewEvents.send(MyShareViewEvent.DeleteShareFailed(error = error))
                }.onCompletion {
                    deleteLock.set(false)
                    _viewStates.update { it.copy(isLoading = false) }
                }.lowestTime().collect {
                    _viewEvents.send(MyShareViewEvent.DeleteShareSuccess(position))
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
        _myShareFlow.getValueData<PageData<Content>>()?.apply {
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

data class MyShareViewState(
    val isLoading: Boolean = false
) : IViewState

sealed class MyShareViewEvent : IViewEvent {
    object ResetSlidingState : MyShareViewEvent()
    data class DeleteShareSuccess(val position: Int) : MyShareViewEvent()
    data class DeleteShareFailed(val error: Throwable) : MyShareViewEvent()
}

sealed class MyShareViewIntent : IViewIntent {
    data class RequestPage(@LoadStatus val status: Int) : MyShareViewIntent()
    data class DeleteShare(val position: Int) : MyShareViewIntent()
}