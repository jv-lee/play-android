package com.lee.pioneer.library.service.hepler

import java.util.*

/**
 * @author jv.lee
 * @data 2021/9/9
 * @description AutoService工具类
 */
object ModuleService {
    inline fun <reified T> find(): T {
        return ServiceLoader.load(T::class.java).iterator().next()
            ?: throw RuntimeException("${T::class.java.name} not module implements")
    }
}