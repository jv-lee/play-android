package com.lee.playandroid.common.extensions

import com.lee.playandroid.base.net.HttpManager
import com.lee.playandroid.base.net.request.IRequest
import com.lee.playandroid.base.net.request.Request
import com.lee.playandroid.common.BuildConfig
import com.lee.playandroid.common.interceptor.FailedInterceptor
import com.lee.playandroid.common.interceptor.HeaderInterceptor
import com.lee.playandroid.common.interceptor.ParameterInterceptor
import com.lee.playandroid.common.interceptor.SaveCookieInterceptor
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

inline fun <reified T> createApi(
    baseUri: String = BuildConfig.BASE_URI,
    request: Request = Request(
        baseUri,
        IRequest.ConverterType.JSON,
        callTypes = intArrayOf(IRequest.CallType.COROUTINE, IRequest.CallType.FLOW)
    )
): T {
    return HttpManager.instance.getService(T::class.java, request)
}