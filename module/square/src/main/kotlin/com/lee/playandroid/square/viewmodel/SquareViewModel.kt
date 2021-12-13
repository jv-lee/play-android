package com.lee.playandroid.square.viewmodel

import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStatePageLiveData
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.square.constants.Constants.CACHE_KEY_SQUARE_CONTENT
import com.lee.playandroid.square.model.repository.ApiRepository

/**
 * @author jv.lee
 * @date 2021/12/13
 * @description 广场ViewModel
 */
class SquareViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    val squareLive = UiStatePageLiveData(0)

    fun requestSquareData(@LoadStatus status: Int) {
        launchIO {
            squareLive.apply {
                pageLaunch(status, { page ->
                    repository.api.getSquareDataSync(page).checkData().also { newData ->
                        applyData(getValueData(), newData)
                    }
                }, {
                    cacheManager.getCache(CACHE_KEY_SQUARE_CONTENT)
                }, {
                    cacheManager.putCache(CACHE_KEY_SQUARE_CONTENT, it)
                })
            }
        }
    }

    init {
        requestSquareData(LoadStatus.INIT)
    }

}