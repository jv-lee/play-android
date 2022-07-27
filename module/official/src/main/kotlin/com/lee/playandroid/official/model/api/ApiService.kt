package com.lee.playandroid.official.model.api

import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.entity.Data
import com.lee.playandroid.common.entity.Tab
import com.lee.playandroid.common.entity.PageData
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 公众号模块api接口
 * @author jv.lee
 * @date 2021/11/8
 */
interface ApiService {

    @GET("wxarticle/chapters/json")
    suspend fun getOfficialTabsAsync(): Data<List<Tab>>

    /**
     * @param id 公众号id 由tab接口获取
     * @param page 分页页面 取值[1-40]
     */
    @GET("wxarticle/list/{id}/{page}/json")
    suspend fun getOfficialDataAsync(
        @Path("id") id: Long,
        @Path("page") page: Int
    ): Data<PageData<Content>>
}