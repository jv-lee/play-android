package com.lee.playandroid.me.model.api

import com.lee.playandroid.library.common.entity.*
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * @author jv.lee
 * @date 2021/11/22
 * @description
 */
interface ApiService {

    /**
     * 获取积分信息
     */
    @GET("lg/coin/userinfo/json")
    suspend fun getCoinInfoAsync(): Data<CoinInfo>

    /**
     * 获取积分记录
     * @param page 1-*
     */
    @GET("lg/coin/list/{page}/json")
    suspend fun getCoinRecordAsync(@Path("page") page: Int): Data<PageData<Coin>>

    /**
     * 收藏文章
     * @param id 文章id
     */
    @POST("lg/collect/{id}/json")
    suspend fun requestCollectAsync(@Path("id") id: Long) : Data<String>

    /**
     * 取消收藏文章
     * @param id 文章id
     */
    @POST("lg/uncollect/{id}/json")
    @FormUrlEncoded
    suspend fun requestUnCollectAsync(@Path("id") id: Long) : Data<String>

    /**
     * 获取收藏记录
     * @param page 0-*
     */
    @GET("lg/collect/list/{page}/json")
    suspend fun getCollectListAsync(@Path("page") page: Int): Data<PageData<Content>>
}