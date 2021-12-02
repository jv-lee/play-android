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
import com.lee.playandroid.system.databinding.ItemContentBinding

/**
 * @author jv.lee
 * @date 2021/11/10
 * @description 体系列表 子内容列表适配器
 */
class ContentListAdapter(context: Context, data: List<Content>) :
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
                    tvTitle.text = HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    tvAuthor.text = if (author.isEmpty()) shareUser else author
                    tvTime.text = TimeUtil.getChineseTimeMill(publishTime)

                    tvCategory.text = when {
                        superChapterName.isNotEmpty() and chapterName.isNotEmpty() -> "$superChapterName / $chapterName"
                        superChapterName.isNotEmpty() -> superChapterName
                        chapterName.isNotEmpty() -> chapterName
                        else -> ""
                    }
                }
            }
        }

    }

}