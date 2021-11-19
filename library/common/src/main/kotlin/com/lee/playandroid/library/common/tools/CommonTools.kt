package com.lee.playandroid.library.common.tools

import android.graphics.Color
import java.util.*

/**
 * @author jv.lee
 * @date 2021/11/19
 * @description
 */
object CommonTools {

    fun getRandomColor(): Int {
        val random = Random()
        return Color.rgb(
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
        )
    }
}