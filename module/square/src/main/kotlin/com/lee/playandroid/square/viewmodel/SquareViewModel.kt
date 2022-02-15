package com.lee.playandroid.square.viewmodel

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

    private val _squareLive = MutableLiveData<UiStatePage>(UiStatePage.Loading(0))
    val squareLive: LiveData<UiStatePage> = _squareLive

    fun requestSquareData(@LoadStatus status: Int) {
        launchIO {
            _squareLive.pageLaunch(status, { page ->
                applyData { repository.api.getSquareDataSync(page).checkData() }
            }, {
                cacheManager.getCache(CACHE_KEY_SQUARE_CONTENT)
            }, {
                cacheManager.putPageCache(CACHE_KEY_SQUARE_CONTENT, it)
            })
        }
    }

    init {
        requestSquareData(LoadStatus.INIT)
    }

}