package com.lee.playandroid.square.viewmodel

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
import com.lee.playandroid.square.constants.Constants.CACHE_KEY_SQUARE_CONTENT
import com.lee.playandroid.square.model.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * 广场页 viewModel
 * @author jv.lee
 * @date 2021/12/13
 */
class SquareViewModel : ViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()
    val accountService = ModuleService.find<AccountService>()

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