package com.lee.playandroid.todo.model.repository

import com.lee.library.mvvm.base.BaseRepository
import com.lee.playandroid.library.common.BuildConfig
import com.lee.playandroid.todo.model.api.ApiService

/**
 * @author jv.lee
 * @date 2021/12/24
 * @description
 */
class ApiRepository : BaseRepository() {
    val api = createApi<ApiService>(BuildConfig.BASE_URI)
}