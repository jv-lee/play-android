package com.lee.playandroid.library.common.ui.extensions

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lee.library.adapter.core.VerticalTabAdapter
import com.lee.library.widget.layoutmanager.LinearTopSmoothScroller

/**
 * @author jv.lee
 * @date 2021/12/22
 * @description 导航页 双RecyclerView联动ui逻辑扩展
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

                    tabRecyclerView.scrollToPosition(position)
                    selectCallback(position)
                }
                isLock = false
            }
        }
    })
}