package com.lee.playandroid.home.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.library.adapter.page.PagingData
import com.lee.library.utils.TimeUtil
import com.lee.pioneer.library.common.entity.Content
import com.lee.playandroid.home.databinding.ItemContentCategoryBinding
import com.lee.playandroid.home.databinding.ItemContentTextBinding
import com.lee.playandroid.home.helper.HomeCategory

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
        return entity is HomeContent.CategoryItem
    }

    override fun convert(holder: ViewBindingHolder, entity: HomeContent, position: Int) {

    }

}

class ContentTextItem : ViewBindingItem<HomeContent>() {

    override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
        return ItemContentTextBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun isItemView(entity: HomeContent, position: Int): Boolean {
        return entity is HomeContent.TextItem
    }

    override fun convert(holder: ViewBindingHolder, entity: HomeContent, position: Int) {
        holder.getViewBinding<ItemContentTextBinding>().apply {

            (entity as HomeContent.TextItem).content.apply {
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

data class PageData<T>(val page: Int, val pageTotal: Int,val data: MutableList<T>) : PagingData<T> {
    override fun getPageNumber(): Int {
        return page
    }

    override fun getPageTotalNumber(): Int {
        return pageTotal
    }

    override fun getDataSource(): List<T> {
        return data
    }

}

sealed class HomeContent {
    class TextItem(val content: Content) : HomeContent()
    class CategoryItem(val data: List<HomeCategory>) : HomeContent()
}