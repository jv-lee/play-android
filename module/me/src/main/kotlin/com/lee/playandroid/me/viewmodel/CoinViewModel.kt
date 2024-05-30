package com.lee.playandroid.me.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.cache.CacheManager
import com.lee.playandroid.base.extensions.getCache
import com.lee.playandroid.base.extensions.putPageCache
import com.lee.playandroid.base.viewstate.*
import com.lee.playandroid.common.extensions.checkData
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.service.AccountService
import com.lee.playandroid.service.hepler.ModuleService
import com.lee.playandroid.me.constants.Constants.CACHE_KEY_COIN_RECORD
import com.lee.playandroid.me.model.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * 积分ViewModel
 * @author jv.lee
 * @date 2021/11/30
 */
class CoinViewModel : ViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()
    val accountService: AccountService = ModuleService.find()

    private val cacheKey = CACHE_KEY_COIN_RECORD.plus(accountService.getUserId())

    private val _coinRecordFlow: UiStatePageMutableStateFlow =
        MutableStateFlow(UiStatePage.Default(1))
    val coinRecordFlow: UiStatePageStateFlow = _coinRecordFlow

    init {
        dispatch(CoinViewIntent.RequestPage(LoadStatus.INIT))
    }

    fun dispatch(intent: CoinViewIntent) {
        when (intent) {
            is CoinViewIntent.RequestPage -> {
                requestCoinRecord(intent.status)
            }
        }
    }

    private fun requestCoinRecord(@LoadStatus status: Int) {
        viewModelScope.launch {
            _coinRecordFlow.pageLaunch(status, { page ->
                applyData { api.getCoinRecordAsync(page).checkData() }
            }, {
                cacheManager.getCache(cacheKey)
            }, {
                cacheManager.putPageCache(cacheKey, it)
            })
        }
    }
}

sealed class CoinViewIntent {
    data class RequestPage(@LoadStatus val status: Int) : CoinViewIntent()
}