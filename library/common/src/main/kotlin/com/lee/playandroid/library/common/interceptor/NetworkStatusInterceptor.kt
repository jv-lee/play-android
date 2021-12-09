package com.lee.playandroid.library.common.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.lang.RuntimeException

/**
 * @author jv.lee
 * @date 2021/11/24
 * @description 网络状态处理拦截器
 */
class NetworkStatusInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response? {
        val request = chain.request()
        chain.call().cancel()
        //Canceled
        return chain.proceed(request)
    }
}