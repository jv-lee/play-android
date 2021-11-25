package com.lee.playandroid.account.model.repository

import com.lee.library.mvvm.base.BaseRepository
import com.lee.playandroid.account.model.api.ApiService
import com.lee.playandroid.library.common.BuildConfig

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description
 */
class ApiRepository : BaseRepository() {
    val api = createApi<ApiService>(BuildConfig.BASE_URI)
}