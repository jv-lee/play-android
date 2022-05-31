package com.lee.playandroid.home.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.library.extensions.setImageTintCompat
import com.lee.playandroid.home.databinding.ItemContentCategoryChildBinding
import com.lee.playandroid.home.model.entity.HomeCategory

/**
 * 首页分类列表适配器 基于
 * @see ContentAdapter.ContentCategoryItem
 * @author jv.lee
 * @date 2021/11/8
 */
class ContentCategoryAdapter(context: Context, data: List<HomeCategory>) :
    ViewBindingAdapter<HomeCategory>(context, data) {

    init {
        addItemStyles(ContentCategoryChildItem())
    }

    /**
     * 每条分类item样式
     */
    inner class ContentCategoryChildItem : ViewBindingItem<HomeCategory>() {

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemContentCategoryChildBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        }

        override fun convert(holder: ViewBindingHolder, entity: HomeCategory, position: Int) {
            holder.getViewBinding<ItemContentCategoryChildBinding>().apply {
                ivCategoryIcon.setImageTintCompat(entity.iconResId)
                tvCategoryName.text = entity.name
            }
        }

    }
}