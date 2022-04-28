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
import com.lee.playandroid.me.constants.Constants.CACHE_KEY_COIN_RANK
import com.lee.playandroid.me.model.api.ApiService
import java.util.*

/**
 * @author jv.lee
 * @date 2021/11/30
 * @description 积分排行榜ViewModel
 */
class CoinRankViewModel : CoroutineViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()
    private val accountService: AccountService = ModuleService.find()

    private val cacheKey = CACHE_KEY_COIN_RANK.plus(accountService.getUserId())

    private val _coinRankLive = UiStatePageMutableLiveData(UiStatePage.Default(1))
    val coinRankLive: UiStatePageLiveData = _coinRankLive

    init {
        requestCoinRank(LoadStatus.INIT)
    }

    fun requestCoinRank(@LoadStatus status: Int) {
        launchIO {
            _coinRankLive.pageLaunch(status, { page ->
                api.getCoinRankAsync(page).checkData().also { newData ->
                    //排行榜UI显示 0 —><- 1 位置数据对掉
                    if (page == _coinRankLive.requestFirstPage && newData.size >= 2) {
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