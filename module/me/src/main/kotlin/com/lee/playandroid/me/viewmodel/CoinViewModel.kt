package com.lee.playandroid.me.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putPageCache
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStatePage
import com.lee.library.mvvm.ui.applyData
import com.lee.library.mvvm.ui.pageLaunch
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

    private val _coinRecordLive = MutableLiveData<UiStatePage>(UiStatePage.Loading(1))
    val coinRecordLive: LiveData<UiStatePage> = _coinRecordLive

    fun requestCoinRecord(@LoadStatus status: Int) {
        launchIO {
            _coinRecordLive.pageLaunch(status, { page ->
                applyData { repository.api.getCoinRecordAsync(page).checkData() }
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