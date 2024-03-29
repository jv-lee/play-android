package com.lee.playandroid.common.constants

import com.lee.playandroid.common.BuildConfig

/**
 * 全局通用api常量
 * @author jv.lee
 * @date 2021/12/3
 */
object ApiConstants {
    /** baseApi */
    const val BASE_URI = BuildConfig.BASE_URI

    /** 站点api请求 成功码 */
    const val REQUEST_OK = 0

    /** 未登陆 错误码 */
    const val REQUEST_TOKEN_ERROR = -1001

    /** 登陆token失效错误自负 */
    const val REQUEST_TOKEN_ERROR_MESSAGE = "TOKEN-ERROR"

    /** 积分规则地址 */
    const val URI_COIN_HELP = "https://www.wanandroid.com/blog/show/2653"
}