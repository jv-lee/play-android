package com.lee.playandroid.system.model.api

import com.lee.playandroid.library.common.entity.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author jv.lee
 * @data 2021/11/8
 * @description
 */
interface ApiService {

    @GET("tree/json")
    suspend fun getParentTabAsync(): Data<List<ParentTab>>

    /**
     * @param page 分页页面 取值[0-40]
     */
    @GET("article/list/{page}/json")
    suspend fun getContentDataAsync(
        @Path("page") page: Int,
        @Query("cid") id: Long
    ): Data<PageData<Content>>

    @GET("navi/json")
    suspend fun getNavigationDataAsync(): Data<List<NavigationItem>>
}