package com.lee.playandroid.square.ui.adapter

import android.content.Context
import com.lee.playandroid.base.adapter.binding.ViewBindingAdapter
import com.lee.playandroid.base.adapter.binding.ViewBindingHolder
import com.lee.playandroid.base.adapter.item.ViewBindingItem
import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.extensions.getAuthor
import com.lee.playandroid.common.extensions.getCategory
import com.lee.playandroid.common.extensions.getDateFormat
import com.lee.playandroid.common.extensions.getTitle
import com.lee.playandroid.square.databinding.ItemSquareBinding

/**
 * 广场列表适配器
 * @author jv.lee
 * @date 2021/12/13
 */
class SquareAdapter(context: Context) :
    ViewBindingAdapter<Content>(context) {

    init {
        addItemStyles(SquareItem())
    }

    inner class SquareItem : ViewBindingItem<ItemSquareBinding, Content>() {

        override fun ItemSquareBinding.convert(
            holder: ViewBindingHolder,
            entity: Content,
            position: Int
        ) {
            entity.apply {
                tvTitle.text = getTitle()
                tvAuthor.text = getAuthor()
                tvTime.text = getDateFormat()
                tvCategory.text = getCategory()
            }
        }
    }
}