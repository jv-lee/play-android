package com.lee.playandroid.me.ui.widget

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


/**
 * @author jv.lee
 * @date 2021/12/3
 * @description RecyclerView Item 拖动/侧滑处理
 */
class RecyclerItemTouchHelper(private val helperCallback: ItemTouchHelperCallback) :
    ItemTouchHelper.Callback() {

    /**
     * 设置滑动类型标记
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     *          返回一个整数类型的标识，用于判断Item那种移动行为是允许的
     */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        //START  右向左 END左向右 LEFT  向左 RIGHT向右  UP向上
        //如果某个值传0，表示不触发该操作，次数设置支持上下拖拽，支持向右滑动
        return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.END)
    }

    /**
     * 拖拽切换Item的回调
     *
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return
     *          如果Item切换了位置，返回true；反之，返回false
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        helperCallback.onMove(viewHolder.layoutPosition, target.layoutPosition)
        return true
    }

    /**
     * 滑动Item
     *
     * @param viewHolder
     * @param direction Item滑动的方向
     *
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        helperCallback.onItemDelete(viewHolder.layoutPosition)
    }

    /**
     * Item被选中时候回调
     *
     * @param viewHolder
     * @param actionState 当前Item的状态
     * ItemTouchHelper.ACTION_STATE_IDLE   闲置状态
     * ItemTouchHelper.ACTION_STATE_SWIPE  滑动中状态
     * ItemTouchHelper#ACTION_STATE_DRAG   拖拽中状态
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
    }

    /**
     * Item是否支持长按拖动
     * @return true  支持长按操作 / false 不支持长按操作
     */
    override fun isLongPressDragEnabled(): Boolean {
        return super.isLongPressDragEnabled()
    }

    /**
     * Item是否支持滑动
     * @return true  支持滑动操作 / false 不支持滑动操作
     */
    override fun isItemViewSwipeEnabled(): Boolean {
        return super.isItemViewSwipeEnabled()
    }

    /**
     * 移动过程中绘制Item
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX X轴移动的距离
     * @param dY Y轴移动的距离
     * @param actionState 当前Item的状态
     * @param isCurrentlyActive 如果当前被用户操作为true，反之为false
     *
     */
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
//        val context = recyclerView.context
//
//        //滑动时自己实现背景及图片
//        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//            //dX大于0时向右滑动，小于0向左滑动
//            val itemView: View = viewHolder.itemView //获取滑动的view
//            val childView = itemView.findViewById<FrameLayout>(R.id.frame_container)
//            //向右滑动
//            if (dX > 0) {
//                itemView.translationX = dX
//            }
//        } else {
//            //拖动时由系统自己完成
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    interface ItemTouchHelperCallback {
        fun onItemDelete(position: Int)
        fun onMove(fromPosition: Int, toPosition: Int)
    }

}