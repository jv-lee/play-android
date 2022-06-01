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
import com.lee.playandroid.me.constants.Constants.CACHE_KEY_COIN_RANK
import com.lee.playandroid.me.model.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*

/**
 * 积分排行榜ViewModel
 * @author jv.lee
 * @date 2021/11/30
 */
class CoinRankViewModel : ViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()
    private val accountService: AccountService = ModuleService.find()

    private val cacheKey = CACHE_KEY_COIN_RANK.plus(accountService.getUserId())

    private val _coinRankFlow: UiStatePageMutableStateFlow =
        MutableStateFlow(UiStatePage.Default(1))
    val coinRankFlow: UiStatePageStateFlow = _coinRankFlow

    init {
        dispatch(CoinRankViewAction.RequestPage(LoadStatus.INIT))
    }

    fun dispatch(action: CoinRankViewAction) {
        when (action) {
            is CoinRankViewAction.RequestPage -> {
                requestCoinRank(action.status)
            }
        }
    }

    private fun requestCoinRank(@LoadStatus status: Int) {
        viewModelScope.launch {
            _coinRankFlow.pageLaunch(status, { page ->
                api.getCoinRankAsync(page).checkData().also { newData ->
                    //排行榜UI显示 0 —><- 1 位置数据对掉
                    if (page == _coinRankFlow.requestFirstPage && newData.size >= 2) {
                        Collections.swap(newData.data, 0, 1)
                    }
                    //内存存储每页数据至LiveData
                    applyData(getValueData(), newData)
                }
            }, {
                cacheManager.getCache(cacheKey)
            }, {
                cacheManager.putPageCache(cacheKey, it)
            })
        }
    }

}

sealed class CoinRankViewAction {
    data class RequestPage(@LoadStatus val status: Int) : CoinRankViewAction()
}

