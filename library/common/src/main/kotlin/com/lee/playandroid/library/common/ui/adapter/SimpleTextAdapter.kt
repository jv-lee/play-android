package com.lee.playandroid.library.common.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.library.utils.TimeUtil
import com.lee.playandroid.library.common.databinding.ItemSimpleTextBinding
import com.lee.playandroid.library.common.entity.Content

/**
 * @author jv.lee
 * @date 2021/12/2
 * @description 简单文本通用adapter
 */
class SimpleTextAdapter(context: Context, data: List<Content>) :
    ViewBindingAdapter<Content>(context, data) {

    init {
        addItemStyles(SimpleTextItem())
    }

    inner class SimpleTextItem : ViewBindingItem<Content>() {
        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemSimpleTextBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun convert(holder: ViewBindingHolder, entity: Content, position: Int) {
            holder.getViewBinding<ItemSimpleTextBinding>().apply {
                tvTitle.text = entity.title
                tvTime.text = TimeUtil.getChineseTimeMill(entity.publishTime)
            }
        }

    }

}

