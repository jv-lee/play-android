package com.lee.playandroid.details.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.UiStateLiveData
import com.lee.library.mvvm.ui.UiStateMutableLiveData
import com.lee.library.mvvm.ui.stateFlow
import com.lee.library.mvvm.vm.CoroutineViewModel
import com.lee.playandroid.details.ui.DetailsFragment
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.service.MeService
import com.lee.playandroid.library.service.hepler.ModuleService
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/12/3
 * @description
 */
class DetailsViewModel(savedStateHandle: SavedStateHandle) : CoroutineViewModel() {

    private val id =
        savedStateHandle.get<String>(DetailsFragment.ARG_PARAMS_ID)?.toLongOrNull() ?: 0

    private var isCollect =
        savedStateHandle.get<String>(DetailsFragment.ARG_PARAMS_COLLECT)?.toBooleanStrictOrNull()
            ?: false

    private val meService = ModuleService.find<MeService>()

    private val _collectLive = UiStateMutableLiveData()
    val collectLive: UiStateLiveData = _collectLive

    fun requestCollect() {
        //已收藏直接返回结果
        if (isCollect) {
            _collectLive.postValue(UiState.Success(false))
            return
        }
        launchIO {
            stateFlow {
                val response = meService.requestCollectAsync(id)
                if (response.errorCode == ApiConstants.REQUEST_OK) {
                    isCollect = true
                    true
                } else {
                    throw RuntimeException(response.errorMsg)
                }
            }.collect {
                _collectLive.postValue(it)
            }
        }
    }

}