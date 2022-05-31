package com.lee.playandroid.account.model.api

import com.lee.playandroid.library.common.entity.AccountData
import com.lee.playandroid.library.common.entity.Data
import com.lee.playandroid.library.common.entity.UserInfo
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 *
 * @author jv.lee
 * @date 2021/11/25
 */
interface ApiService {

    @GET("user/lg/userinfo/json")
    suspend fun getAccountInfoAsync(): Data<AccountData>

    @POST("user/login")
    @FormUrlEncoded
    suspend fun postLoginAsync(
        @Field("username") username: String,
        @Field("password") password: String
    ): Data<UserInfo>

    @POST("user/register")
    @FormUrlEncoded
    suspend fun postRegisterAsync(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") rePassword: String
    ): Data<UserInfo>

    @GET("user/logout/json")
    suspend fun getLogoutAsync(): Data<Any>

}