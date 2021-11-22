package com.lee.playandroid.search.ui.adapter

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
import com.lee.playandroid.library.common.tools.GlideTools
import com.lee.playandroid.search.databinding.ItemSearchResultPictureBinding
import com.lee.playandroid.search.databinding.ItemSearchResultTextBinding

/**
 * @author jv.lee
 * @date 2021/11/22
 * @description
 */
class SearchResultAdapter(context: Context, data: List<Content>) :
    ViewBindingAdapter<Content>(context, data) {

    init {
        addItemStyles(SearchResultTextItem())
        addItemStyles(SearchResultPictureItem())
    }

    inner class SearchResultTextItem : ViewBindingItem<Content>() {
        override fun isItemView(entity: Content, position: Int): Boolean {
            return entity.envelopePic.isEmpty()
        }

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemSearchResultTextBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        }

        override fun convert(holder: ViewBindingHolder, entity: Content, position: Int) {
            holder.getViewBinding<ItemSearchResultTextBinding>().apply {
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

    inner class SearchResultPictureItem : ViewBindingItem<Content>() {

        override fun isItemView(entity: Content, position: Int): Boolean {
            return entity.envelopePic.isNotEmpty()
        }

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemSearchResultPictureBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        }

        override fun convert(holder: ViewBindingHolder, entity: Content, position: Int) {
            holder.getViewBinding<ItemSearchResultPictureBinding>().apply {
                entity.apply {
                    GlideTools.get().loadImage(envelopePic, ivImage)

                    tvTitle.text = HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    tvDescription.text = desc
                    tvAuthor.text = if (author.isEmpty()) shareUser else author
                    tvTime.text = TimeUtil.getChineseTimeMill(publishTime)
                }
            }
        }

    }

}