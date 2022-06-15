package com.lee.playandroid.home.view.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.lee.playandroid.home.R
import com.lee.playandroid.home.view.adapter.ContentAdapter

/**
 * 首页列表添加文案Decoration样式
 * @author jv.lee
 * @date 2021/11/10
 */
class LabelDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private var textHeight: Float
    private var marginItem: Float
    private var textLabel: String

    init {
        textPaint.color = ContextCompat.getColor(context, R.color.colorThemeAccent)
        textPaint.textSize = context.resources.getDimension(R.dimen.font_size_large_xx)
        textPaint.typeface = Typeface.DEFAULT_BOLD

        textHeight = textPaint.descent() - textPaint.ascent()
        marginItem = context.resources.getDimension(R.dimen.medium_offset)

        textLabel = context.getString(R.string.home_recommend_label)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (isFirstType(view, parent)) {
            outRect.top = (textHeight + marginItem).toInt()
        } else {
            outRect.top = 0
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        parent.children.forEach {
            if (isFirstType(it, parent)) {
                val left = it.left + marginItem
                val top = it.top - marginItem
                c.drawText(textLabel, left, top, textPaint)
            }
        }
    }

    private fun isFirstType(view: View, parent: RecyclerView): Boolean {
        val position = parent.getChildAdapterPosition(view)
        val type = parent.adapter?.getItemViewType(position)
        if (position == 0 && type == ContentAdapter.CONTENT_TEXT_ITEM_TYPE) {
            return true
        }

        val prevType = parent.adapter?.getItemViewType(position - 1)
        if (prevType != ContentAdapter.CONTENT_TEXT_ITEM_TYPE && type == ContentAdapter.CONTENT_TEXT_ITEM_TYPE) {
            return true
        }

        return false
    }

}