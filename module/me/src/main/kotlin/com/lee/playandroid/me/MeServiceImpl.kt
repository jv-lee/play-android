package com.lee.playandroid.me

import com.google.auto.service.AutoService
import com.lee.playandroid.common.entity.Data
import com.lee.playandroid.common.extensions.createApi
import com.lee.playandroid.service.MeService
import com.lee.playandroid.me.model.api.ApiService

/**
 *
 * @author jv.lee
 * @date 2021/12/3
 */
@AutoService(MeService::class)
class MeServiceImpl : MeService {

    override suspend fun requestCollectAsync(id: Long): Data<String> {
        return createApi<ApiService>().postCollectAsync(id)
    }

}