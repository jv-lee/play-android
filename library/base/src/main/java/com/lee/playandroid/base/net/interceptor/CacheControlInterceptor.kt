package com.lee.playandroid.base.net.interceptor

import com.lee.playandroid.base.base.ApplicationExtensions.app
import com.lee.playandroid.base.utils.NetworkUtil
import java.io.IOException
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 缓存控制拦截器
 * @author jv.lee
 * @date 2020/3/20
 */
class CacheControlInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!NetworkUtil.isNetworkConnected(app)) {
            request = request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()
        }
        val response = chain.proceed(request)
        if (NetworkUtil.isNetworkConnected(app)) {
            val maxAge = 60 * 60 // read from cache for 1 minute
            response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, max-age=$maxAge")
                .build()
        } else {
            val maxStale = 60 * 60 * 24 * 28 // tolerate 4-weeks stale
            response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                .build()
        }
        return response
    }
}