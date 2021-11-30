package com.lee.playandroid.me.model.api

import com.lee.playandroid.library.common.entity.Coin
import com.lee.playandroid.library.common.entity.CoinInfo
import com.lee.playandroid.library.common.entity.Data
import com.lee.playandroid.library.common.entity.PageData
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author jv.lee
 * @date 2021/11/22
 * @description
 */
interface ApiService {

    @GET("lg/coin/userinfo/json")
    suspend fun getCoinInfoAsync(): Data<CoinInfo>

    @GET("lg/coin/list/{page}/json")
    suspend fun getCoinRecordAsync(@Path("page") page: Int): Data<PageData<Coin>>
}