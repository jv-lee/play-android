package com.lee.playandroid.library.common.extensions

import com.lee.playandroid.library.common.entity.Data

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description
 */
fun <T> Data<T>.checkData(): T {
    if (errorCode == 0) {
        return data
    }
    throw RuntimeException(errorMsg)
}