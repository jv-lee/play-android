package com.lee.playandroid.library.service

import com.lee.playandroid.library.common.entity.Data

/**
 * @author jv.lee
 * @date 2021/12/3
 * @description
 */
interface MeService {
    suspend fun requestCollectAsync(id: Long): Data<String>
}