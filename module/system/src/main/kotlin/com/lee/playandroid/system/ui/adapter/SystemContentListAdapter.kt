package com.lee.playandroid.system.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.library.utils.TimeUtil
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.extensions.getAuthor
import com.lee.playandroid.library.common.extensions.getCategory
import com.lee.playandroid.library.common.extensions.getDateFormat
import com.lee.playandroid.library.common.extensions.getTitle
import com.lee.playandroid.system.databinding.ItemContentBinding

/**
 * @author jv.lee
 * @date 2021/11/10
 * @description 体系列表 子内容列表适配器
 */
class SystemContentListAdapter(context: Context, data: List<Content>) :
    ViewBindingAdapter<Content>(context, data) {

    init {
        addItemStyles(ContentItem())
    }

    inner class ContentItem : ViewBindingItem<Content>() {
        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemContentBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun convert(holder: ViewBindingHolder, entity: Content, position: Int) {
            holder.getViewBinding<ItemContentBinding>().apply {
                entity.apply {
                    tvTitle.text = getTitle()
                    tvAuthor.text = getAuthor()
                    tvTime.text = getDateFormat()
                    tvCategory.text = getCategory()
                }
            }
        }

    }

}