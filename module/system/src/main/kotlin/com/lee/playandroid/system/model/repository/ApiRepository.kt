package com.lee.playandroid.system.model.repository

import com.lee.library.mvvm.base.BaseRepository
import com.lee.library.net.HttpManager
import com.lee.library.net.request.IRequest
import com.lee.library.net.request.Request
import com.lee.playandroid.library.common.BuildConfig
import com.lee.playandroid.system.model.api.ApiService

/**
 * @author jv.lee
 * @data 2021/11/2
 * @description
 */
class ApiRepository : BaseRepository() {

    val api: ApiService

    init {
        val request = Request(
            BuildConfig.BASE_URI,
            IRequest.ConverterType.JSON,
            callTypes = intArrayOf(IRequest.CallType.COROUTINE, IRequest.CallType.FLOW)
        )
        api = HttpManager.getInstance().getService(ApiService::class.java, request)
    }
}