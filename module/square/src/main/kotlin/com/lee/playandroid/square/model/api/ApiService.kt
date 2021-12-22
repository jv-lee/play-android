package com.lee.playandroid.square.model.api

import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.Data
import com.lee.playandroid.library.common.entity.MyShareData
import com.lee.playandroid.library.common.entity.PageData
import retrofit2.http.*

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

    /**
     * @param page 分页页面 取值[1-40]
     */
    @GET("/user/lg/private_articles/{page}/json")
    suspend fun getMyShareDataSync(@Path("page") page: Int): Data<MyShareData>

    /**
     * 分享文章
     * @param title 分享文章标题
     * @param link 分享文章链接
     */
    @POST("lg/user_article/add/json")
    @FormUrlEncoded
    suspend fun postShareDataSync(
        @Field("title") title: String,
        @Field("link") link: String
    ): Data<String>

}