package com.lee.playandroid.account.model.api

import com.lee.playandroid.library.common.entity.AccountData
import com.lee.playandroid.library.common.entity.Data
import com.lee.playandroid.library.common.entity.UserInfo
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description
 */
interface ApiService {

    @GET("user/lg/userinfo/json")
    suspend fun getAccountInfo(): Data<AccountData>

    @POST("user/login")
    @FormUrlEncoded
    suspend fun requestLogin(
        @Field("username") username: String,
        @Field("password") password: String
    ): Data<UserInfo>

    @POST("user/register")
    @FormUrlEncoded
    suspend fun requestRegister(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") rePassword: String
    ): Data<UserInfo>

    @GET("user/logout/json")
    suspend fun requestLogout(): Data<Any>

}