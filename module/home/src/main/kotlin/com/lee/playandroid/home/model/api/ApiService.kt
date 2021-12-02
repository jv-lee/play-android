package com.lee.playandroid.home.model.api

import com.lee.playandroid.library.common.entity.Banner
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.Data
import com.lee.playandroid.library.common.entity.PageData
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author jv.lee
 * @date 2021/11/2
 * @description
 */
interface ApiService {
    @GET("banner/json")
    suspend fun getBannerDataAsync(): Data<List<Banner>>

    /**
     * @param page 分页页面 取值[0-40]
     */
    @GET("article/list/{page}/json")
    suspend fun getContentDataAsync(@Path("page") page: Int): Data<PageData<Content>>
}