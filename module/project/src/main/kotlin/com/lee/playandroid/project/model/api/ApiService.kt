package com.lee.playandroid.project.model.api

import com.lee.pioneer.library.common.entity.Content
import com.lee.pioneer.library.common.entity.Data
import com.lee.pioneer.library.common.entity.PageData
import com.lee.pioneer.library.common.entity.Tab
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author jv.lee
 * @data 2021/11/8
 * @description
 */
interface ApiService {

    @GET("project/tree/json")
    suspend fun getProjectTabsAsync(): Data<List<Tab>>

    /**
     * @param id 项目id 由tab接口获取
     * @param page 分页页面 取值[1-40]
     */
    @GET("project/list/{page}/json")
    suspend fun getProjectDataAsync(
        @Path("page") page: Int,
        @Query("cid") id: Long
    ): Data<PageData<Content>>
}