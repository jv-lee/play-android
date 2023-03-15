package com.lee.playandroid.base.adapter.binding

import android.content.Context
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.playandroid.base.adapter.base.BaseViewAdapter
import com.lee.playandroid.base.adapter.base.BaseViewHolder

/**
 * [BaseViewAdapter] 的viewBinding实现 ,使用viewBinding解析view时可使用该适配器基类
 * @author jv.lee
 * @date 2019/3/29
 */
open class ViewBindingAdapter<T>(context: Context) : BaseViewAdapter<T>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        // 根据布局的类型 创建不同的ViewHolder
        val item = getItemStyles().getViewItem(viewType)
            ?: throw RuntimeException("itemStyle.getViewItem is null.")
        val viewBinding = item.getItemViewAny(parent.context, parent) as? ViewBinding
            ?: throw RuntimeException("itemStyle.getItemViewAny is null.")

        val viewHolder = ViewBindingHolder(viewBinding)

        // 点击的监听
        if (item.openClick()) {
            setListener(viewHolder, item.openShake())
            // 子view监听
            setChildListener(viewHolder, item.openShake())
        }
        return viewHolder
    }
}