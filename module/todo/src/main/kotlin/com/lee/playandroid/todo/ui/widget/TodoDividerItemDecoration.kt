package com.lee.playandroid.todo.ui.widget

import android.R
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author jv.lee
 * @date 2021/12/27
 * @description
 */
class TodoDividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private var mDivider: Drawable?

    private val mBounds = Rect()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawDivider(c, parent)
    }

    private fun drawDivider(canvas: Canvas, parent: RecyclerView) {
        mDivider ?: return
        canvas.save()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if (isTextType(child, parent)) {
                parent.getDecoratedBoundsWithMargins(child, mBounds)
                val bottom: Int = mBounds.bottom + Math.round(child.translationY)
                val top: Int = bottom - mDivider?.intrinsicHeight!!
                mDivider?.setBounds(left, top, right, bottom)
                mDivider?.draw(canvas)
            }
        }
        canvas.restore()
    }

    private fun isTextType(view: View, parent: RecyclerView): Boolean {
        val position = parent.getChildAdapterPosition(view)
        val type = parent.adapter?.getItemViewType(position)
        if (type == 0) {
            return true
        }
        return false
    }

    init {
        val a: TypedArray = context.obtainStyledAttributes(intArrayOf(R.attr.listDivider))
        mDivider = a.getDrawable(0)
        a.recycle()
    }

}