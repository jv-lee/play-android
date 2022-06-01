package com.lee.playandroid.common.extensions

import com.lee.playandroid.common.constants.ApiConstants
import com.lee.playandroid.common.entity.Data

/**
 *
 * @author jv.lee
 * @date 2021/11/25
 */
fun <T> Data<T>.checkData(): T {
    if (errorCode == ApiConstants.REQUEST_OK) {
        return data
    }else if (errorCode == ApiConstants.REQUEST_TOKEN_ERROR) {
        throw RuntimeException(ApiConstants.REQUEST_TOKEN_ERROR_MESSAGE)
    }
    throw RuntimeException(errorMsg)
}