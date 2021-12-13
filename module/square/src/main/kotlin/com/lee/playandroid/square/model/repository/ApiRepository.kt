package com.lee.playandroid.square.model.repository

import com.lee.library.mvvm.base.BaseRepository
import com.lee.playandroid.library.common.BuildConfig
import com.lee.playandroid.square.model.api.ApiService

/**
 * @author jv.lee
 * @date 2021/12/13
 * @description
 */
class ApiRepository : BaseRepository() {
    val api: ApiService = createApi(BuildConfig.BASE_URI)
}