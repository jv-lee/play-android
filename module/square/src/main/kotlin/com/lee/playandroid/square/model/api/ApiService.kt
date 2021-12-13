package com.lee.playandroid.square.model.api

import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.Data
import com.lee.playandroid.library.common.entity.PageData
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author jv.lee
 * @date 2021/12/13
 * @description
 */
interface ApiService {

    /**
     * @param page 分页页面 取值[0-40]
     */
    @GET("/user_article/list/{page}/json")
    suspend fun getSquareDataSync(@Path("page") page: Int): Data<PageData<Content>>
}