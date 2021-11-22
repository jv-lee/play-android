package com.lee.playandroid.me.model.repository

import com.lee.library.mvvm.base.BaseRepository
import com.lee.playandroid.library.common.BuildConfig
import com.lee.playandroid.me.model.api.ApiService

/**
 * @author jv.lee
 * @date 2021/11/22
 * @description
 */
class ApiRepository : BaseRepository() {
    val api = createApi<ApiService>(BuildConfig.BASE_URI)
}