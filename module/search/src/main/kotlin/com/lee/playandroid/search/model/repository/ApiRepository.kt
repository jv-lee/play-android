package com.lee.playandroid.search.model.repository

import com.lee.library.mvvm.base.BaseRepository
import com.lee.playandroid.library.common.BuildConfig
import com.lee.playandroid.search.model.api.ApiService

/**
 * @author jv.lee
 * @date 2021/11/19
 * @description
 */
class ApiRepository : BaseRepository() {
    val api: ApiService = createApi(BuildConfig.BASE_URI)
}