package com.lee.playandroid.square.viewmodel

import androidx.lifecycle.viewModelScope
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putPageCache
import com.lee.library.viewmodel.CoroutineViewModel
import com.lee.library.viewstate.*
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.square.constants.Constants.CACHE_KEY_SQUARE_CONTENT
import com.lee.playandroid.square.model.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2021/12/13
 * @description 广场ViewModel
 */
class SquareViewModel : CoroutineViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()

    private val _squareFlow: UiStatePageMutableStateFlow =
        MutableStateFlow(UiStatePage.Default(0))
    val squareFlow: UiStatePageStateFlow = _squareFlow

    init {
        requestSquareData(LoadStatus.INIT)
    }

    fun dispatch(action: SquareViewAction) {
        when (action) {
            is SquareViewAction.RequestPage -> {
                requestSquareData(action.status)
            }
        }
    }

    private fun requestSquareData(@LoadStatus status: Int) {
        viewModelScope.launch {
            _squareFlow.pageLaunch(status, { page ->
                applyData { api.getSquareDataSync(page).checkData() }
            }, {
                cacheManager.getCache(CACHE_KEY_SQUARE_CONTENT)
            }, {
                cacheManager.putPageCache(CACHE_KEY_SQUARE_CONTENT, it)
            })
        }
    }

}

sealed class SquareViewAction {
    data class RequestPage(@LoadStatus val status: Int) : SquareViewAction()
}