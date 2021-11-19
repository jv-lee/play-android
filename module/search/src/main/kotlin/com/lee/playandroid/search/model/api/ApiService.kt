package com.lee.playandroid.search.model.api

import retrofit2.http.Field
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * @author jv.lee
 * @date 2021/11/19
 * @description
 */
interface ApiService {

    @POST("/article/query/{page}}/json")
    fun requestSearchByAsync(@Path("page") page: Int, @Field("k") key: String)

}