package com.lee.playandroid.me.ui.widget

import android.content.res.Resources
import android.graphics.*
import android.view.View
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
//        return super.isItemViewSwipeEnabled()
        return isSwipeEnable
    }

    private var isSwipeEnable = true

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
        //滑动时自己实现背景及图片
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            //dX大于0时向右滑动，小于0向左滑动
            val itemView: View = viewHolder.itemView //获取滑动的view
            val resources: Resources = recyclerView.context.resources
            val bitmap: Bitmap = BitmapFactory.decodeResource(
                resources,
                android.R.mipmap.sym_def_app_icon
            ) //获取删除指示的背景图片
            val padding = 10 //图片绘制的padding
            val maxDrawWidth: Int = 2 * padding + bitmap.width //最大的绘制宽度
            val paint = Paint()
            paint.color = Color.RED
            val x = Math.round(Math.abs(dX))
            val drawWidth = Math.min(x, maxDrawWidth) //实际的绘制宽度，取实时滑动距离x和最大绘制距离maxDrawWidth最小值
            val itemTop: Int = itemView.getBottom() - itemView.getHeight() //绘制的top位置
            //向右滑动
            if (dX > 0) {
                //根据滑动实时绘制一个背景
                c.drawRect(
                    itemView.left.toFloat(), itemTop.toFloat(),
                    drawWidth.toFloat(), itemView.bottom.toFloat(), paint
                )
                //在背景上面绘制图片
                if (x > padding) { //滑动距离大于padding时开始绘制图片
                    //指定图片绘制的位置
                    val rect = Rect() //画图的位置
                    rect.left = itemView.left + padding
                    rect.top =
                        itemTop + (itemView.bottom - itemTop - bitmap.height) / 2 //图片居中
                    val maxRight: Int = rect.left + bitmap.width
                    rect.right = Math.min(x, maxRight)
                    rect.bottom = rect.top + bitmap.height
                    //指定图片的绘制区域
                    var rect1: Rect? = null
                    if (x < maxRight) {
                        rect1 = Rect() //不能再外面初始化，否则dx大于画图区域时，删除图片不显示
                        rect1.left = 0
                        rect1.top = 0
                        rect1.bottom = bitmap.height
                        rect1.right = x - padding
                    }
                    c.drawBitmap(bitmap, rect1, rect, paint)
                }
                //绘制时需调用平移动画，否则滑动看不到反馈
                itemView.translationX = dX
            } else {
                //如果在getMovementFlags指定了向左滑动（ItemTouchHelper。START）时则绘制工作可参考向右的滑动绘制，也可直接使用下面语句交友系统自己处理
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        } else {
            //拖动时有系统自己完成
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
    }

    interface ItemTouchHelperCallback {
        fun onItemDelete(position: Int)
        fun onMove(fromPosition: Int, toPosition: Int)
    }

}