package com.lee.playandroid.service

import com.lee.playandroid.common.entity.Data

/**
 *
 * @author jv.lee
 * @date 2021/12/3
 */
interface MeService {
    suspend fun requestCollectAsync(id: Long): Data<String>
}