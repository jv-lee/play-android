package com.lee.playandroid.search.extensions

import android.graphics.Color
import java.util.*

/**
 * @author jv.lee
 * @date 2021/12/30
 * @description
 */
fun getRandomColor(): Int {
    val random = Random()
    return Color.rgb(
        random.nextInt(256),
        random.nextInt(256),
        random.nextInt(256)
    )
}