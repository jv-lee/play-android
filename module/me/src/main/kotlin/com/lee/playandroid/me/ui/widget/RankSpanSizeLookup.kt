package com.lee.playandroid.me.ui.widget

import androidx.recyclerview.widget.GridLayoutManager

/**
 *
 * @author jv.lee
 * @date 2021/12/9
 */
open class RankSpanSizeLookup : GridLayoutManager.SpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        return if (position < 3) 1 else 3
    }
}
