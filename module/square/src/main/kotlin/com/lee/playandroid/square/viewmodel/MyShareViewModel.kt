package com.lee.playandroid.square.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.extensions.putPageCache
import com.lee.library.mvvm.annotation.LoadStatus
import com.lee.library.mvvm.ui.*
import com.lee.library.mvvm.vm.CoroutineViewModel
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.square.constants.Constants
import com.lee.playandroid.square.model.api.ApiService
import kotlinx.coroutines.flow.collect
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author jv.lee
 * @date 2021/12/16
 * @description 我的分享列表
 */
class MyShareViewModel : CoroutineViewModel() {

    private val api = createApi<ApiService>()

    private val cacheManager = CacheManager.getDefault()

    private val deleteLock = AtomicBoolean(false)

    private val _deleteShareLive = UiStateMutableLiveData()
    val deleteShareLive: UiStateLiveData = _deleteShareLive

    private val _myShareLive = MutableLiveData<UiStatePage>(UiStatePage.Default(1))
    val myShareLive: LiveData<UiStatePage> = _myShareLive

    fun requestMyShareData(@LoadStatus status: Int) {
        launchIO {
            _myShareLive.pageLaunch(status, { page ->
                api.getMyShareDataSync(page)
                    .checkData().shareArticles.also { newData ->
                        applyData(getValueData(), newData)
                    }
            }, {
                cacheManager.getCache(Constants.CACHE_KEY_MY_SHARE_CONTENT)
            }, {
                cacheManager.putPageCache(Constants.CACHE_KEY_MY_SHARE_CONTENT, it)
            })
        }
    }

    fun requestDeleteShare(position: Int) {
        if (deleteLock.get()) return
        deleteLock.set(true)

        launchIO {
            stateFlow {
                val data = myShareLive.getValueData<PageData<Content>>()!!
                val item = data.data[position]

                val response = api.postDeleteShareAsync(item.id)
                if (response.errorCode == ApiConstants.REQUEST_OK) {
                    removeCacheItem(item)
                    position
                } else {
                    throw RuntimeException(response.errorMsg)
                }
            }.collect {
                deleteLock.set(false)
                _deleteShareLive.postValue(it)
            }
        }
    }

    /**
     * 更新首页缓存
     * @param content 被更新的数据实体
     */
    private fun removeCacheItem(content: Content) {
        // 内存移除
        myShareLive.getValueData<PageData<Content>>()?.apply {
            this.data.remove(content)
        }

        // 缓存移除
        cacheManager.getCache<PageData<Content>>(Constants.CACHE_KEY_MY_SHARE_CONTENT)?.apply {
            if (data.remove(content)) {
                cacheManager.putCache(Constants.CACHE_KEY_MY_SHARE_CONTENT, this)
            }
        }
    }

}