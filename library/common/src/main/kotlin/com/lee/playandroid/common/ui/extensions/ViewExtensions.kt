/*
 * 项目通用view扩展函数
 * @author jv.lee
 * @date 2022/1/5
 */
package com.lee.playandroid.common.ui.extensions

import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lee.playandroid.base.adapter.core.VerticalTabAdapter
import com.lee.playandroid.base.widget.layoutmanager.LinearTopSmoothScroller
import com.lee.playandroid.common.R

/**
 * 通用主题渐变背景
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

/**
 * 导航页 双RecyclerView联动ui逻辑扩展
 */
fun VerticalTabAdapter<*>.bindTabLinkage(
    tabRecyclerView: RecyclerView,
    contentRecyclerView: RecyclerView,
    selectCallback: (Int) -> Unit
) {
    var isLock = false
    setItemClickCall(object : VerticalTabAdapter.ItemClickCall {
        override fun itemClick(position: Int) {
            contentRecyclerView.apply {
                val scroller = LinearTopSmoothScroller(context, position)
                layoutManager?.startSmoothScroll(scroller)
            }

            selectCallback(position)
            isLock = true
        }
    })

    contentRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (!isLock) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val position = layoutManager.findFirstVisibleItemPosition()

                    recyclerView.post {
                        tabRecyclerView.scrollToPosition(position)
                        selectCallback(position)
                    }
                }
                isLock = false
            }
        }
    })
}