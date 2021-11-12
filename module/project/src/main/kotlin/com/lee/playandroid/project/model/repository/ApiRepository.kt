package com.lee.playandroid.project.model.repository

import com.lee.library.mvvm.base.BaseRepository
import com.lee.library.net.HttpManager
import com.lee.library.net.request.IRequest
import com.lee.library.net.request.Request
import com.lee.playandroid.library.common.BuildConfig
import com.lee.playandroid.project.model.api.ApiService

/**
 * @author jv.lee
 * @data 2021/11/2
 * @description
 */
class ApiRepository : BaseRepository() {

    val api: ApiService = createApi(BuildConfig.BASE_URI)
}