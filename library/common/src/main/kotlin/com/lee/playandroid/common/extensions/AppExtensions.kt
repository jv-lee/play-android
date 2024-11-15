/*
 * app通用扩展
 * @author jv.lee
 * @date 2022/3/7
 */
package com.lee.playandroid.common.extensions

import android.app.Activity
import androidx.core.content.ContextCompat
import com.lee.playandroid.base.tools.DarkModeTools
import com.lee.playandroid.base.tools.SystemBarTools.setDarkStatusIcon
import com.lee.playandroid.base.tools.SystemBarTools.setLightStatusIcon
import com.lee.playandroid.common.R

/**
 * activity主题根据深色模式适配
 */
fun Activity.appThemeSet() {
    window.navigationBarColor = ContextCompat.getColor(this, R.color.colorThemeWindow)
    // 主题icon适配
    if (DarkModeTools.get().isDarkTheme()) {
        window.setLightStatusIcon()
    } else {
        window.setDarkStatusIcon()
    }
}

/**
 * 判断当前activity是否为内部activity，过滤第三方库activity不执行 [block] 方法块
 */
fun Activity.runInternalBlock(block: () -> Unit) {
    val appPackageName = packageName.replace(".debug", "")
    if (this::class.java.name.contains(appPackageName)) {
        block()
    }
}