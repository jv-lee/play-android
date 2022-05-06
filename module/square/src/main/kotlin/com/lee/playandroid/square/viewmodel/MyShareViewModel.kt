package com.lee.playandroid.square.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.extensions.putPageCache
import com.lee.library.viewstate.*
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService
import com.lee.playandroid.square.constants.Constants.CACHE_KEY_MY_SHARE_CONTENT
import com.lee.playandroid.square.model.api.ApiService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author jv.lee
 * @date 2021/12/16
 * @description 我的分享列表
 */
class MyShareViewModel : ViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()
    private val accountService: AccountService = ModuleService.find()

    private val cacheKey = CACHE_KEY_MY_SHARE_CONTENT.plus(accountService.getUserId())
    private val deleteLock = AtomicBoolean(false)

    private val _myShareFlow: UiStatePageMutableStateFlow = MutableStateFlow(UiStatePage.Default(1))
    val myShareFlow: StateFlow<UiStatePage> = _myShareFlow

    private val _viewEvents = Channel<MyShareViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    init {
        dispatch(MyShareViewAction.RequestPage(LoadStatus.INIT))
    }

    fun dispatch(action: MyShareViewAction) {
        when (action) {
            is MyShareViewAction.RequestPage -> {
                requestMyShareData(action.status)
            }
            is MyShareViewAction.DeleteShare -> {
                requestDeleteShare(action.position)
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
                    val data = _myShareFlow.getValueData<PageData<Content>>()!!
                    val item = data.data[position]

                    val response = api.postDeleteShareAsync(item.id)
                    if (response.errorCode == ApiConstants.REQUEST_OK) {
                        removeCacheItem(item)
                        emit(position)
                    } else {
                        throw RuntimeException(response.errorMsg)
                    }
                }.catch { error ->
                    deleteLock.set(false)
                    _viewEvents.send(MyShareViewEvent.DeleteShareFailed(error = error))
                }.collect {
                    deleteLock.set(false)
                    _viewEvents.send(MyShareViewEvent.DeleteShareSuccess)
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

sealed class MyShareViewAction {
    data class RequestPage(@LoadStatus val status: Int) : MyShareViewAction()
    data class DeleteShare(val position: Int) : MyShareViewAction()
}

sealed class MyShareViewEvent {
    object DeleteShareSuccess : MyShareViewEvent()
    data class DeleteShareFailed(val error: Throwable) : MyShareViewEvent()
}