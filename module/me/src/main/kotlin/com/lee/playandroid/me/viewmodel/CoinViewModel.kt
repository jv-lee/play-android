package com.lee.playandroid.me.viewmodel

import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putPageCache
import com.lee.library.mvvm.annotation.LoadStatus
import com.lee.library.mvvm.ui.*
import com.lee.library.mvvm.vm.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.me.constants.Constants.CACHE_KEY_COIN_RECORD
import com.lee.playandroid.me.model.api.ApiService

/**
 * @author jv.lee
 * @date 2021/11/30
 * @description 积分ViewModel
 */
class CoinViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val api = createApi<ApiService>()

    private val _coinRecordLive = UiStatePageMutableLiveData(UiStatePage.Default(1))
    val coinRecordLive: UiStatePageLiveData = _coinRecordLive

    fun requestCoinRecord(@LoadStatus status: Int) {
        launchIO {
            _coinRecordLive.pageLaunch(status, { page ->
                applyData { api.getCoinRecordAsync(page).checkData() }
            }, {
                cacheManager.getCache(CACHE_KEY_COIN_RECORD)
            }, {
                cacheManager.putPageCache(CACHE_KEY_COIN_RECORD, it)
            })
        }
    }

    init {
        requestCoinRecord(LoadStatus.INIT)
    }

}