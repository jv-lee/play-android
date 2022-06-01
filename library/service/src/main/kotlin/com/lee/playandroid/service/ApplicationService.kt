package com.lee.playandroid.service

import android.app.Application
import com.lee.playandroid.service.core.IModuleService

/**
 *
 * @author jv.lee
 * @date 2021/9/9
 */
interface ApplicationService : IModuleService {
    fun init(application: Application)
}