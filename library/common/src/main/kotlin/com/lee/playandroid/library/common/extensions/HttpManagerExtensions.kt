package com.lee.playandroid.library.common.extensions

import com.lee.library.net.HttpManager
import com.lee.library.net.request.IRequest
import com.lee.library.net.request.Request
import com.lee.playandroid.library.common.BuildConfig
import com.lee.playandroid.library.common.interceptor.FailedInterceptor
import com.lee.playandroid.library.common.interceptor.HeaderInterceptor
import com.lee.playandroid.library.common.interceptor.ParameterInterceptor
import com.lee.playandroid.library.common.interceptor.SaveCookieInterceptor
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