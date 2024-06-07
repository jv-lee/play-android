package com.lee.playandroid.search.ui.adapter

import android.content.Context
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
import com.lee.playandroid.search.R
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

    inner class SearchResultTextItem : ViewBindingItem<ItemSearchResultTextBinding, Content>() {
        override fun isItemView(entity: Content, position: Int): Boolean {
            return entity.envelopePic.isEmpty()
        }

        override fun ItemSearchResultTextBinding.convert(
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

    inner class SearchResultPictureItem :
        ViewBindingItem<ItemSearchResultPictureBinding, Content>() {

        override fun isItemView(entity: Content, position: Int): Boolean {
            return entity.envelopePic.isNotEmpty()
        }

        override fun ItemSearchResultPictureBinding.convert(
            holder: ViewBindingHolder,
            entity: Content,
            position: Int
        ) {
            entity.apply {
                root.context.resources.apply {
                    ivImage.shapeAppearanceModel = ShapeAppearanceModel.Builder()
                        .setTopLeftCornerSize(getDimension(R.dimen.offset_radius_medium))
                        .setBottomLeftCornerSize(getDimension(R.dimen.offset_radius_medium))
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
}