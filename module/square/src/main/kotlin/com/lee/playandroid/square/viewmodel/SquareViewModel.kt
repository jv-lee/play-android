package com.lee.playandroid.square.viewmodel

import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putPageCache
import com.lee.library.mvvm.ui.*
import com.lee.library.viewmodel.CoroutineViewModel
import com.lee.library.viewstate.*
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.square.constants.Constants.CACHE_KEY_SQUARE_CONTENT
import com.lee.playandroid.square.model.api.ApiService

/**
 * @author jv.lee
 * @date 2021/12/13
 * @description 广场ViewModel
 */
class SquareViewModel : CoroutineViewModel() {

    private val api = createApi<ApiService>()

    private val cacheManager = CacheManager.getDefault()

    private val _squareLive = UiStatePageMutableLiveData(UiStatePage.Default(0))
    val squareLive: UiStatePageLiveData = _squareLive

    fun requestSquareData(@_root_ide_package_.com.lee.library.viewstate.LoadStatus status: Int) {
        launchIO {
            _squareLive.pageLaunch(status, { page ->
                applyData { api.getSquareDataSync(page).checkData() }
            }, {
                cacheManager.getCache(CACHE_KEY_SQUARE_CONTENT)
            }, {
                cacheManager.putPageCache(CACHE_KEY_SQUARE_CONTENT, it)
            })
        }
    }

    init {
        requestSquareData(_root_ide_package_.com.lee.library.viewstate.LoadStatus.INIT)
    }

}