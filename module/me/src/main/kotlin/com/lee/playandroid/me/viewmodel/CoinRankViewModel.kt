package com.lee.playandroid.me.viewmodel

import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putPageCache
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.*
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.me.constants.Constants.CACHE_KEY_COIN_RANK
import com.lee.playandroid.me.model.repository.ApiRepository
import java.util.*

/**
 * @author jv.lee
 * @date 2021/11/30
 * @description 积分排行榜ViewModel
 */
class CoinRankViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    private val _coinRankLive = UiStatePageMutableLiveData(UiStatePage.Loading(1))
    val coinRankLive: UiStatePageLiveData = _coinRankLive

    fun requestCoinRank(@LoadStatus status: Int) {
        launchIO {
            _coinRankLive.pageLaunch(status, { page ->
                repository.api.getCoinRankAsync(page).checkData().also { newData ->
                    //排行榜UI显示 0 —><- 1 位置数据对掉
                    if (page == _coinRankLive.requestFirstPage && newData.size >= 2) {
                        Collections.swap(newData.data, 0, 1)
                    }
                    //内存存储每页数据至LiveData
                    applyData(getValueData(), newData)
                }
            }, {
                cacheManager.getCache(CACHE_KEY_COIN_RANK)
            }, {
                cacheManager.putPageCache(CACHE_KEY_COIN_RANK, it)
            })
        }
    }

    init {
        requestCoinRank(LoadStatus.INIT)
    }

}