package com.lee.playandroid.square.model.api

import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.entity.Data
import com.lee.playandroid.common.entity.MyShareData
import com.lee.playandroid.common.entity.PageData
import retrofit2.http.*

/**
 * 广场模块api接口
 * @author jv.lee
 * @date 2021/12/13
 */
interface ApiService {

    /**
     * 广场列表
     * @param page 分页页面 取值[0-40]
     */
    @GET("/user_article/list/{page}/json")
    suspend fun getSquareDataSync(@Path("page") page: Int): Data<PageData<Content>>

    /**
     * 我的分享列表
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

    /**
     * 删除我的分享文章
     * @param id 文章id
     */
    @POST("/lg/user_article/delete/{id}/json")
    suspend fun postDeleteShareAsync(@Path("id") id: Long): Data<String>
}