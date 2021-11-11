package com.lee.playandroid.home.view.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author jv.lee
 * @data 2021/11/11
 * @description
 */
class OffsetTopDecoration(private val offsetTop: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildLayoutPosition(view)
        if (position == 0) {
            outRect.top = offsetTop
        }
    }

}