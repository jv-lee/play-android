package com.lee.playandroid.service.hepler

import android.app.Application
import com.lee.playandroid.service.core.ApplicationService
import java.util.*

/**
 * 各模块application初始化回调
 * @author jv.lee
 * @date 2021/9/9
 */
object ApplicationModuleService {
    fun init(application: Application) {
        val iterator = ServiceLoader.load(ApplicationService::class.java).iterator()
        while (iterator.hasNext()) {
            iterator.next().init(application)
        }
    }
}