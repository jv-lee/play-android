package com.lee.playandroid.common.ui.extensions

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lee.playandroid.base.adapter.core.VerticalTabAdapter
import com.lee.playandroid.base.widget.layoutmanager.LinearTopSmoothScroller

/**
 * 导航页 双RecyclerView联动ui逻辑扩展
 * @author jv.lee
 * @date 2021/12/22
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