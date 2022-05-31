package com.lee.playandroid.library.common.ui.extensions

import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.content.ContextCompat
import com.lee.playandroid.library.common.R

/**
 *
 * @author jv.lee
 * @date 2022/1/5
 */

fun View.setThemeGradientBackground() {
    val color = ContextCompat.getColor(context, R.color.colorThemeBackground)
    val transparent = ContextCompat.getColor(context, R.color.colorThemeBackgroundTransparent)

    val backgroundDrawable = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(color, color, color, color, color, transparent)
    )
    backgroundDrawable.setGradientCenter(0F, 0.1F)
    backgroundDrawable.shape = GradientDrawable.RECTANGLE
    background = backgroundDrawable
}