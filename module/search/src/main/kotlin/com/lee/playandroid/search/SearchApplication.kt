package com.lee.playandroid.search

import android.app.Application
import com.google.auto.service.AutoService
import com.lee.playandroid.library.service.ApplicationService
import com.lee.playandroid.search.model.db.SearchHistoryDatabase

/**
 * @author jv.lee
 * @date 2021/11/22
 * @description
 */
@AutoService(ApplicationService::class)
class SearchApplication : ApplicationService {
    override fun init(application: Application) {
        SearchHistoryDatabase.getInstance(application)
    }
}