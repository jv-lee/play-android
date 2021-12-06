package com.lee.playandroid.library.common.extensions

import com.lee.library.net.HttpManager
import com.lee.playandroid.library.common.BuildConfig
import com.lee.playandroid.library.common.interceptor.SaveCookieInterceptor
import com.lee.playandroid.library.common.interceptor.FailedInterceptor
import com.lee.playandroid.library.common.interceptor.HeaderInterceptor
import com.lee.playandroid.library.common.interceptor.ParameterInterceptor
import okhttp3.logging.HttpLoggingInterceptor

/**
 * 设置通用拦截器
 */
fun HttpManager.setCommonInterceptor() {
    val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE
    }

    putInterceptor(httpLoggingInterceptor)
    putInterceptor(FailedInterceptor())
    putInterceptor(ParameterInterceptor())
    putInterceptor(HeaderInterceptor())
    putInterceptor(SaveCookieInterceptor())
}