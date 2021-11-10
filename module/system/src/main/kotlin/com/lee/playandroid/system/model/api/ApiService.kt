package com.lee.playandroid.system.model.api

import com.lee.playandroid.library.common.entity.Data
import com.lee.playandroid.library.common.entity.ParentTab
import retrofit2.http.GET

/**
 * @author jv.lee
 * @data 2021/11/8
 * @description
 */
interface ApiService {

    @GET("tree/json")
    suspend fun getParentTabAsync(): Data<List<ParentTab>>

}