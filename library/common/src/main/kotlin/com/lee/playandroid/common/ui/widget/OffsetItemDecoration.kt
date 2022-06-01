package com.lee.playandroid.common.ui.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * @author jv.lee
 * @date 2021/11/11
 */
class OffsetItemDecoration(
    private val offsetTop: Int = 0,
    private val offsetBottom: Int = 0
) : RecyclerView.ItemDecoration() {

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
        parent.adapter?.apply {
            if (position == itemCount - 1) {
                outRect.bottom = offsetBottom
            }
        }

    }

}