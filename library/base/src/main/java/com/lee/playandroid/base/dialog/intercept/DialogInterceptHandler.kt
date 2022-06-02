package com.lee.playandroid.base.dialog.intercept

/**
 *
 * @author jv.lee
 * @date 2021/8/26
 */
class DialogInterceptHandler<T> {
    var firstIntercept: DialogIntercept<T>? = null

    fun add(interceptChain: DialogIntercept<T>) {
        if (firstIntercept == null) {
            firstIntercept = interceptChain
            return
        }

        var node = firstIntercept
        while (true) {
            if (node?.next == null) {
                node?.next = interceptChain
                break
            }
            node = node.next
        }
    }

    fun intercept(item: T) {
        firstIntercept?.intercept(item)
    }

}