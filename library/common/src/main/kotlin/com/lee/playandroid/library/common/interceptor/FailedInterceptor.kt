package com.lee.playandroid.library.common.interceptor

import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.Response
import java.nio.charset.Charset

/**
 * @author jv.lee
 * @date 2021/11/24
 * @description
 */
class FailedInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        try {
            response.body()?.apply {
                val contentLength = contentLength()
                val source = source()
                source.request(Long.MAX_VALUE)

                val buffer = source.buffer()
                val charset = Charset.forName("UTF-8")

                if (contentLength != 0L) {
                    val body = buffer.clone().readString(charset)
                    val json = JsonParser.parseString(body)
                    val code = json.asJsonObject.get("errorCode").asInt
                    val message = json.asJsonObject.get("errorMsg").asString
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return response
    }
}