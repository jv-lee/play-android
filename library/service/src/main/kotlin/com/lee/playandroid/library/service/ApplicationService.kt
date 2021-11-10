package com.lee.playandroid.library.service

import android.app.Application
import com.lee.playandroid.library.service.core.IModuleService

/**
 * @author jv.lee
 * @date 2021/9/9
 * @description
 */
interface ApplicationService : IModuleService {
    fun init(application: Application)
}