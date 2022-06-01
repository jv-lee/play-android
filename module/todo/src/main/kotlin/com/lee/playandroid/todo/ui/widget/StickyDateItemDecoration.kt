package com.lee.playandroid.todo.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lee.playandroid.base.extensions.dp2px
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.ui.adapter.TodoListAdapter

/**
 * TODO列表吸附时间头item
 * @author jv.lee
 * @date 2021/12/28
 */
class StickyDateItemDecoration constructor(
    private val context: Context,
    private val adapter: TodoListAdapter?
) :
    RecyclerView.ItemDecoration() {

    private var topOffset: Int = context.dp2px(26).toInt()
    private var bottomOffset: Int = context.dp2px(1).toInt()

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        mPaint.color = ContextCompat.getColor(context, R.color.colorThemeFocusLight)
        mPaint.style = Paint.Style.FILL

        textPaint.typeface = Typeface.DEFAULT_BOLD
        textPaint.textSize = context.resources.getDimension(R.dimen.font_size_small)
        textPaint.color = ContextCompat.getColor(context, R.color.colorThemeFocus)
        textPaint.textAlign = Paint.Align.LEFT
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        if (isFirstInGroup(position)) {
            outRect.top = topOffset
        } else {
            outRect.top = 0
        }

        val groupKey = getGroupKey(position)
        val nextGroupKey = getGroupKey(position + 1)
        if (groupKey == nextGroupKey) {
            outRect.bottom = bottomOffset
        } else {
            outRect.bottom = 0
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val childCount = parent.childCount
        val itemCount = parent.adapter?.itemCount ?: 0
        val left = parent.left
        val right = parent.right

        var prevGroupKey = ""
        var groupKey = ""

        for (index in 0..childCount) {
            val view = parent.getChildAt(index)
            val position = parent.getChildAdapterPosition(view)

            prevGroupKey = groupKey
            groupKey = getGroupKey(position)
            if (groupKey == "" || groupKey == prevGroupKey) continue

            val dateText = getGroupKey(position)
            if (TextUtils.isEmpty(dateText)) continue

            val viewBottom = view.bottom
            var textY = Math.max(topOffset, view.top)

            if (position + 1 < itemCount) {
                val nextGroupKey = getGroupKey(position + 1)
                if (nextGroupKey != groupKey && viewBottom < textY) {
                    textY = viewBottom
                }
            }

            c.drawRect(
                left.toFloat(), (textY - topOffset).toFloat(),
                right.toFloat(), textY.toFloat(), mPaint
            )

            val rect = Rect()
            textPaint.getTextBounds(dateText, 0, dateText.length, rect)
            c.drawText(
                dateText, (rect.width() / 4).toFloat(),
                (textY - (topOffset - rect.height()) / 2).toFloat(), textPaint
            )
        }
    }

    private fun isFirstInGroup(position: Int): Boolean {
        if (position == 0) return true

        val prevGroupKey = getGroupKey(position - 1)
        val groupKey = getGroupKey(position)
        return prevGroupKey != groupKey
    }

    private fun getGroupKey(position: Int): String {
        adapter ?: return ""
        val data = adapter.data
        if (position < 0 || position >= data.size) return ""
        return data[position].dateStr
    }

}