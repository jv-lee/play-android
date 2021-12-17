package com.lee.playandroid.me.viewmodel

import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.extensions.putPageCache
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.me.constants.Constants.CACHE_KEY_COIN_RECORD
import com.lee.playandroid.me.model.repository.ApiRepository

/**
 * @author jv.lee
 * @date 2021/11/30
 * @description 积分ViewModel
 */
class CoinViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    val coinRecordLive = UiStatePageLiveData(1)

    fun requestCoinRecord(@LoadStatus status: Int) {
        launchIO {
            coinRecordLive.apply {
                pageLaunch(status, { page ->
                    repository.api.getCoinRecordAsync(page).checkData().also { newData ->
                        applyData(getValueData(), newData)
                    }
                }, {
                    cacheManager.getCache(CACHE_KEY_COIN_RECORD)
                }, {
                    cacheManager.putPageCache(CACHE_KEY_COIN_RECORD, it)
                })
            }
        }
    }

    init {
        requestCoinRecord(LoadStatus.INIT)
    }

}