package com.lee.playandroid.block

import android.content.Context
import com.github.moduth.blockcanary.BlockCanaryContext
import com.github.moduth.blockcanary.internal.BlockInfo
import com.lee.playandroid.base.utils.LogUtil
import com.lee.playandroid.BuildConfig
import com.lee.playandroid.R

/**
 * BlockCanaryContext定制
 * @author jv.lee
 * @date 2022/2/9
 */
class AppBlockCanaryContext : BlockCanaryContext() {
    override fun provideQualifier(): String {
        return provideContext().getString(R.string.app_name) + BuildConfig.VERSION_NAME
    }

    override fun provideUid(): String {
        return BuildConfig.VERSION_CODE.toString()
    }

    override fun provideNetworkType(): String {
        return "wifi"
    }

    /**
     * 卡断伐值 毫秒级
     */
    override fun provideBlockThreshold(): Int {
        return 500
    }

    /**
     * 卡顿时dump主线程间隔时间
     */
    override fun provideDumpInterval(): Int {
        return provideBlockThreshold()
    }

    /**
     * 卡顿日志保存目录
     */
    override fun providePath(): String {
        return "/blockCanary_log/"
    }

    /**
     * 卡断时是否显示通知提示
     */
    override fun displayNotification(): Boolean {
        return true
    }

    /**
     * 卡顿时触发该回调
     */
    override fun onBlock(context: Context?, blockInfo: BlockInfo?) {
        LogUtil.i("onBlock:$blockInfo")
    }
}