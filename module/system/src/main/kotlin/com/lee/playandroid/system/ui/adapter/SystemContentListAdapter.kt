package com.lee.playandroid.system.ui.adapter

import android.content.Context
import com.lee.playandroid.base.adapter.binding.ViewBindingAdapter
import com.lee.playandroid.base.adapter.binding.ViewBindingHolder
import com.lee.playandroid.base.adapter.item.ViewBindingItem
import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.extensions.getAuthor
import com.lee.playandroid.common.extensions.getCategory
import com.lee.playandroid.common.extensions.getDateFormat
import com.lee.playandroid.common.extensions.getTitle
import com.lee.playandroid.system.databinding.ItemContentBinding

/**
 * 体系列表 子内容列表适配器
 * @author jv.lee
 * @date 2021/11/10
 */
class SystemContentListAdapter(context: Context) :
    ViewBindingAdapter<Content>(context) {

    init {
        addItemStyles(ContentItem())
    }

    inner class ContentItem : ViewBindingItem<ItemContentBinding, Content>() {

        override fun ItemContentBinding.convert(
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