package com.lee.playandroid.base.widget.toolbar.menu

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.lee.playandroid.base.R

/**
 * 自定义简单功能的 menuItem
 * @author jv.lee
 * @date 2020/4/22
 */
class CustomMenuItemView constructor(context: Context) : FrameLayout(context) {

    private val tvText: TextView

    init {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).also {
            it.gravity = Gravity.CENTER
        }
        val layoutItem = LayoutInflater.from(context).inflate(R.layout.layout_menu_item, this, true)
        tvText = layoutItem.findViewById(R.id.tv_text)
    }

    fun getTitle(): TextView {
        return tvText
    }
}