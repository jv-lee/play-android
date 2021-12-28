package com.lee.playandroid.library.common.interceptor

import com.lee.library.tools.PreferencesTools
import com.lee.playandroid.library.common.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author jv.lee
 * @date 2021/11/24
 * @description 请求头部设置拦截器
 */
class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()

        builder.addHeader("Content-type", "application/json; charset=utf-8")

        val url = request.url().toString()
        if (url.contains(BuildConfig.BASE_URI)) {
            val token: String = PreferencesTools.get(BuildConfig.BASE_URI)

            if (token.isNotEmpty()) {
                builder.addHeader("Cookie", token)
            }
        }
        return chain.proceed(builder.build())
    }

}