package com.lee.playandroid.account.model.api

import com.lee.playandroid.library.common.entity.AccountData
import com.lee.playandroid.library.common.entity.Data
import retrofit2.http.GET

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description
 */
interface ApiService {

    @GET("user/lg/userinfo/json")
    suspend fun getAccountInfo(): Data<AccountData>

}