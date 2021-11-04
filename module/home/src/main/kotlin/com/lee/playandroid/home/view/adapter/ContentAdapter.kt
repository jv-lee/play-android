package com.lee.playandroid.home.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.library.utils.TimeUtil
import com.lee.playandroid.home.bean.HomeContent
import com.lee.playandroid.home.databinding.ItemContentCategoryBinding
import com.lee.playandroid.home.databinding.ItemContentTextBinding

/**
 * @author jv.lee
 * @data 2021/11/2
 * @description
 */
class ContentAdapter(context: Context, data: List<HomeContent>) :
    ViewBindingAdapter<HomeContent>(context, data) {

    init {
        addItemStyles(ContentCategoryItem())
        addItemStyles(ContentTextItem())
    }
}

class ContentCategoryItem : ViewBindingItem<HomeContent>() {
    override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
        return ItemContentCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun isItemView(entity: HomeContent, position: Int): Boolean {
        return entity.categoryList != null
    }

    override fun convert(holder: ViewBindingHolder, entity: HomeContent, position: Int) {

    }

}

class ContentTextItem : ViewBindingItem<HomeContent>() {

    override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
        return ItemContentTextBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun isItemView(entity: HomeContent, position: Int): Boolean {
        return entity.content != null
    }

    override fun convert(holder: ViewBindingHolder, entity: HomeContent, position: Int) {
        holder.getViewBinding<ItemContentTextBinding>().apply {

            entity.content?.apply {
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