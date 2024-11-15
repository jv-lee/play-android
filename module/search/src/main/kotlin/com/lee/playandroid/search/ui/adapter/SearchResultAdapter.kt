package com.lee.playandroid.search.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.shape.ShapeAppearanceModel
import com.lee.playandroid.base.adapter.binding.ViewBindingAdapter
import com.lee.playandroid.base.adapter.binding.ViewBindingHolder
import com.lee.playandroid.base.adapter.item.ViewBindingItem
import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.extensions.getAuthor
import com.lee.playandroid.common.extensions.getCategory
import com.lee.playandroid.common.extensions.getDateFormat
import com.lee.playandroid.common.extensions.getTitle
import com.lee.playandroid.common.tools.GlideTools
import com.lee.playandroid.common.R as CR
import com.lee.playandroid.search.databinding.ItemSearchResultPictureBinding
import com.lee.playandroid.search.databinding.ItemSearchResultTextBinding

/**
 * 搜索结果列表适配器
 * @author jv.lee
 * @date 2021/11/22
 */
class SearchResultAdapter(context: Context) :
    ViewBindingAdapter<Content>(context) {

    init {
        addItemStyles(SearchResultTextItem())
        addItemStyles(SearchResultPictureItem())
    }

    inner class SearchResultTextItem : ViewBindingItem<Content>() {
        override fun isItemView(entity: Content, position: Int): Boolean {
            return entity.envelopePic.isEmpty()
        }

        override fun convert(
            holder: ViewBindingHolder,
            entity: Content,
            position: Int
        ) {
            holder.getViewBinding<ItemSearchResultTextBinding>().apply {
                entity.apply {
                    tvTitle.text = getTitle()
                    tvAuthor.text = getAuthor()
                    tvTime.text = getDateFormat()
                    tvCategory.text = getCategory()
                }
            }
        }

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemSearchResultTextBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        }
    }

    inner class SearchResultPictureItem :
        ViewBindingItem<Content>() {

        override fun isItemView(entity: Content, position: Int): Boolean {
            return entity.envelopePic.isNotEmpty()
        }

        override fun convert(
            holder: ViewBindingHolder,
            entity: Content,
            position: Int
        ) {
            holder.getViewBinding<ItemSearchResultPictureBinding>().apply {
                entity.apply {
                    root.context.resources.apply {
                        ivImage.shapeAppearanceModel = ShapeAppearanceModel.Builder()
                            .setTopLeftCornerSize(getDimension(CR.dimen.offset_radius_medium))
                            .setBottomLeftCornerSize(getDimension(CR.dimen.offset_radius_medium))
                            .build()
                    }

                    GlideTools.get().loadImage(envelopePic, ivImage)

                    tvTitle.text = entity.getTitle()
                    tvDescription.text = desc
                    tvAuthor.text = entity.getAuthor()
                    tvTime.text = entity.getDateFormat()
                }
            }
        }

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemSearchResultPictureBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        }
    }
}