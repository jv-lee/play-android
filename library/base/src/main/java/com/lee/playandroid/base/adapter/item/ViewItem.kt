package com.lee.playandroid.base.adapter.item

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.lee.playandroid.base.adapter.base.BaseViewHolder
import com.lee.playandroid.base.adapter.base.BaseViewItem

/**
 * viewItem 类型类
 * @author jv.lee
 * @date 2021/6/15
 */
abstract class ViewItem<T> : BaseViewItem<T> {

    override fun getItemViewAny(context: Context, parent: ViewGroup): Any {
        return getItemView(context, parent)
    }

    override fun viewRecycled(holder: BaseViewHolder) {
    }

    abstract fun getItemView(context: Context, parent: ViewGroup): View
}