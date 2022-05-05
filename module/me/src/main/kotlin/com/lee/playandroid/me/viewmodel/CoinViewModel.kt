package com.lee.playandroid.me.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putPageCache
import com.lee.library.viewstate.*
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService
import com.lee.playandroid.me.constants.Constants.CACHE_KEY_COIN_RECORD
import com.lee.playandroid.me.model.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2021/11/30
 * @description 积分ViewModel
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
        dispatch(CoinViewAction.RequestPage(LoadStatus.INIT))
    }

    fun dispatch(action: CoinViewAction) {
        when (action) {
            is CoinViewAction.RequestPage -> {
                requestCoinRecord(action.status)
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

sealed class CoinViewAction {
    data class RequestPage(@LoadStatus val status: Int) : CoinViewAction()
}