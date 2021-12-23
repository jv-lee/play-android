package com.lee.playandroid.details.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.details.ui.DetailsFragment
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.service.MeService
import com.lee.playandroid.library.service.hepler.ModuleService

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

    private val _collectLive = MutableLiveData<Boolean>()
    val collectLive: LiveData<Boolean> = _collectLive

    fun requestCollect() {
        //已收藏直接返回结果
        if (isCollect) {
            _collectLive.postValue(false)
            return
        }
        launchIO {
            val response = meService.requestCollectAsync(id)
            if (response.errorCode == ApiConstants.REQUEST_OK) {
                isCollect = true
                _collectLive.postValue(true)
            } else {
                throw RuntimeException(response.errorMsg)
            }
        }
    }

}