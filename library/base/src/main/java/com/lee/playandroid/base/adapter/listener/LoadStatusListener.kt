package com.lee.playandroid.base.adapter.listener

/**
 * 当前适配器加载状态接口回调
 * @author jv.lee
 * @date 2020/11/28
 */
interface LoadStatusListener {
    fun onChangeStatus(status: Int)
}