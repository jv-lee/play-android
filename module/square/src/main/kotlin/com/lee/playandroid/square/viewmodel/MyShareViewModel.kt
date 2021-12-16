package com.lee.playandroid.square.viewmodel

import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.square.constants.Constants
import com.lee.playandroid.square.model.repository.ApiRepository

/**
 * @author jv.lee
 * @date 2021/12/16
 * @description 我的分享列表
 */
class MyShareViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    val myShareLive = UiStatePageLiveData(1)

    fun requestMyShareData(@LoadStatus status: Int) {
        launchIO {
            myShareLive.apply {
                pageLaunch(status, { page ->
                    repository.api.getMyShareDataSync(page)
                        .checkData().shareArticles.also { newData ->
                        applyData(getValueData(), newData)
                    }
                }, {
                    cacheManager.getCache(Constants.CACHE_KEY_MY_SHARE_CONTENT)
                }, {
                    cacheManager.putCache(Constants.CACHE_KEY_MY_SHARE_CONTENT, it)
                })
            }
        }
    }

    init {
        requestMyShareData(LoadStatus.INIT)
    }


}