package com.lee.playandroid.me

import com.google.auto.service.AutoService
import com.lee.playandroid.library.common.entity.Data
import com.lee.playandroid.library.service.MeService
import com.lee.playandroid.me.model.repository.ApiRepository

/**
 * @author jv.lee
 * @date 2021/12/3
 * @description
 */
@AutoService(MeService::class)
class MeServiceImpl : MeService {

    override suspend fun requestCollectAsync(id: Long): Data<String> {
        return ApiRepository().api.requestCollectAsync(id)
    }

}