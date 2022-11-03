package com.lee.playandroid.search.model.api

import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.entity.Data
import com.lee.playandroid.common.entity.PageData
import com.lee.playandroid.common.entity.SearchHot
import retrofit2.http.*

/**
 * 搜索模块api接口
 * @author jv.lee
 * @date 2021/11/19
 */
interface ApiService {

    @POST("article/query/{page}/json")
    @FormUrlEncoded
    suspend fun postSearchAsync(
        @Path("page") page: Int,
        @Field("k") key: String
    ): Data<PageData<Content>>

    @GET("hotkey/json")
    suspend fun getSearchHotAsync(): Data<List<SearchHot>>
}