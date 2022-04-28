package com.lee.playandroid.me.viewmodel

import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putPageCache
import com.lee.library.viewmodel.CoroutineViewModel
import com.lee.library.viewstate.*
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService
import com.lee.playandroid.me.constants.Constants.CACHE_KEY_COIN_RECORD
import com.lee.playandroid.me.model.api.ApiService

/**
 * @author jv.lee
 * @date 2021/11/30
 * @description 积分ViewModel
 */
class CoinViewModel : CoroutineViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()
    private val accountService: AccountService = ModuleService.find()

    private val cacheKey = CACHE_KEY_COIN_RECORD.plus(accountService.getUserId())

    private val _coinRecordLive = UiStatePageMutableLiveData(UiStatePage.Default(1))
    val coinRecordLive: UiStatePageLiveData = _coinRecordLive

    fun requestCoinRecord(@LoadStatus status: Int) {
        launchIO {
            _coinRecordLive.pageLaunch(status, { page ->
                applyData { api.getCoinRecordAsync(page).checkData() }
            }, {
                cacheManager.getCache(cacheKey)
            }, {
                cacheManager.putPageCache(cacheKey, it)
            })
        }
    }

    init {
        requestCoinRecord(LoadStatus.INIT)
    }

}