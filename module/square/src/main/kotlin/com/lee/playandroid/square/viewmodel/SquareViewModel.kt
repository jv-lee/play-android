package com.lee.playandroid.square.viewmodel

import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putPageCache
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.PageUiState
import com.lee.library.mvvm.ui.applyData
import com.lee.library.mvvm.ui.pageLaunch
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.square.constants.Constants.CACHE_KEY_SQUARE_CONTENT
import com.lee.playandroid.square.model.repository.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @author jv.lee
 * @date 2021/12/13
 * @description 广场ViewModel
 */
class SquareViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    val squareFlow = MutableStateFlow<PageUiState>(PageUiState.Default(0))

    fun requestSquareData(@LoadStatus status: Int) {
        launchIO {
            squareFlow.pageLaunch(status, { page ->
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