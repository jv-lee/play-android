package com.lee.playandroid.home.view.adapter

import android.content.Context
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.library.utils.TimeUtil
import com.lee.pioneer.library.common.entity.Content
import com.lee.playandroid.home.databinding.ItemContentTextBinding

/**
 * @author jv.lee
 * @data 2021/11/2
 * @description
 */
class ContentAdapter(context: Context, data: List<Content>) :
    ViewBindingAdapter<Content>(context, data) {

    init {
        addItemStyles(ContentTextItem())
    }
}

class ContentTextItem : ViewBindingItem<Content>() {

    override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
        return ItemContentTextBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun convert(holder: ViewBindingHolder, entity: Content, position: Int) {
        holder.getViewBinding<ItemContentTextBinding>().apply {

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