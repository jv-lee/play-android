/*
 * app通用扩展
 * @author jv.lee
 * @date 2022/3/7
 */
package com.lee.playandroid.common.extensions

import android.app.Activity
import android.graphics.Color
import com.lee.playandroid.base.tools.DarkModeTools
import com.lee.playandroid.base.tools.StatusTools.setDarkStatusIcon
import com.lee.playandroid.base.tools.StatusTools.setLightStatusIcon
import com.lee.playandroid.base.tools.StatusTools.setNavigationBarColor

fun Activity.appThemeSet() {
    // 主题icon适配
    if (DarkModeTools.get().isDarkTheme()) {
        setNavigationBarColor(Color.BLACK)
        setLightStatusIcon()
    } else {
        setNavigationBarColor(Color.WHITE)
        setDarkStatusIcon()
    }
}