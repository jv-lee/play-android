package com.lee.playandroid.library.common.interceptor

import com.google.gson.JsonParser
import com.lee.library.livedatabus.LiveDataBus
import com.lee.library.tools.PreferencesTools
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.common.entity.LoginEvent
import okhttp3.Interceptor
import okhttp3.Response
import java.nio.charset.Charset

/**
 * @author jv.lee
 * @date 2021/11/24
 * @description 全局统一错误拦截器
 * 处理错误码对应的逻辑
 */
class FailedInterceptor : Interceptor {

    companion object {
        private const val RESPONSE_CODE = "errorCode"
        private const val SP_KEY_IS_LOGIN = "spKey:is-login"
    }

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
                    val code = json.asJsonObject.get(RESPONSE_CODE).asInt
                    //登陆失效,打开登陆页面
                    if (code == ApiConstants.REQUEST_TOKEN_ERROR) {
                        //单独处理登陆状态 ， 已登陆状态发起重新登陆事件
                        if (PreferencesTools.get(SP_KEY_IS_LOGIN)) {
                            LiveDataBus.instance.getChannel(LoginEvent.key)
                                .postValue(LoginEvent())
                        }
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return response
    }
}