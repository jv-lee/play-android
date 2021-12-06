package com.lee.playandroid.library.common.interceptor

import com.lee.library.tools.PreferencesTools
import com.lee.playandroid.library.common.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author jv.lee
 * @date 2021/11/24
 * @description Cookie处理拦截器 用于保存账号cookie信息
 */
class SaveCookieInterceptor : Interceptor {

    companion object {
        private const val SET_COOKIE_KEY = "set-cookie"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val url = request.url().toString()

        if (url.contains(BuildConfig.BASE_URI) && response.headers(SET_COOKIE_KEY).isNotEmpty()) {
            val cookies = response.headers(SET_COOKIE_KEY)
            val cookie = encodeCookie(cookies)
            saveCookie(cookie)
        }

        return response
    }

    private fun encodeCookie(cookies: List<String>): String {
        val sb = StringBuilder()
        val set = HashSet<String>()
        cookies
            .map { cookie ->
                cookie.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            }
            .forEach {
                it.filterNot { set.contains(it) }.forEach { set.add(it) }
            }
        val ite = set.iterator()
        while (ite.hasNext()) {
            val cookie = ite.next()
            sb.append(cookie).append(";")
        }
        val last = sb.lastIndexOf(";")
        if (sb.length - 1 == last) {
            sb.deleteCharAt(last)
        }
        return sb.toString()
    }

    private fun saveCookie(cookie: String) {
        PreferencesTools.put(BuildConfig.BASE_URI, cookie)
    }

}