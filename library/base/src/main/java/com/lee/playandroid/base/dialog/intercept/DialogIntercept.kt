package com.lee.playandroid.base.dialog.intercept

/**
 *
 * @author jv.lee
 * @date 2021/8/26
 */
abstract class DialogIntercept<T> {
    var next: DialogIntercept<T>? = null

    open fun intercept(item: T) {
        next?.intercept(item)
    }
}