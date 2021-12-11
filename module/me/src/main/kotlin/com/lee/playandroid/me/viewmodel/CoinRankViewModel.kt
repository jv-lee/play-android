package com.lee.playandroid.me.viewmodel

import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.entity.CoinRank
import com.lee.playandroid.library.common.entity.PageData
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

    val coinRankLive = UiStatePageLiveData(1)

    fun requestCoinRank(@LoadStatus status: Int) {
        launchIO {
            coinRankLive.apply {
                pageLaunch(status, { page ->
                    repository.api.getCoinRankAsync(page).checkData().also { newData ->
                        //排行榜UI显示 0 —><- 1 位置数据对掉
                        if (page == coinRankLive.getInitPage() && newData.size >= 2) {
                            Collections.swap(newData.data, 0, 1)
                        }
                        //内存存储每页数据至LiveData
                        applyData(getValueData<PageData<CoinRank>>(), newData)
                    }
                }, {
                    cacheManager.getCache(CACHE_KEY_COIN_RANK)
                }, {
                    cacheManager.putCache(CACHE_KEY_COIN_RANK, it)
                })
            }
        }
    }

    init {
        requestCoinRank(LoadStatus.INIT)
    }

}