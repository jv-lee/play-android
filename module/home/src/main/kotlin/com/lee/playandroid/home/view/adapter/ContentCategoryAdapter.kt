package com.lee.playandroid.home.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.playandroid.home.databinding.ItemContentCategoryChildBinding
import com.lee.playandroid.home.helper.HomeCategory

/**
 * @author jv.lee
 * @data 2021/11/8
 * @description
 */
class ContentCategoryAdapter(context: Context, data: List<HomeCategory>) :
    ViewBindingAdapter<HomeCategory>(context, data) {

    init {
        addItemStyles(ContentCategoryChildItem())
    }
}

class ContentCategoryChildItem() : ViewBindingItem<HomeCategory>() {

    override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
        return ItemContentCategoryChildBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun convert(holder: ViewBindingHolder, entity: HomeCategory, position: Int) {
        holder.getViewBinding<ItemContentCategoryChildBinding>().apply {
            ivCategoryIcon.setImageResource(entity.iconResId)
            tvCategoryName.text = entity.name
        }
    }

}