package com.lee.playandroid.library.common.extensions

import android.app.Activity
import android.graphics.Color
import com.lee.library.tools.DarkModeTools
import com.lee.library.tools.StatusTools.setDarkStatusIcon
import com.lee.library.tools.StatusTools.setLightStatusIcon
import com.lee.library.tools.StatusTools.setNavigationBarColor

/**
 * @author jv.lee
 * @date 2022/3/7
 * @description
 */

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