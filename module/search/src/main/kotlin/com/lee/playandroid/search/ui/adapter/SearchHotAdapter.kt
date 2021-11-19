package com.lee.playandroid.search.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.playandroid.library.common.tools.CommonTools
import com.lee.playandroid.search.databinding.ItemSearchHotBinding
import com.lee.playandroid.search.helper.SearchHot

/**
 * @author jv.lee
 * @date 2021/11/19
 * @description
 */
class SearchHotAdapter(context: Context, data: List<SearchHot>) :
    ViewBindingAdapter<SearchHot>(context, data) {

    init {
        addItemStyles(SearchHotItemViewHolder())
    }

    inner class SearchHotItemViewHolder : ViewBindingItem<SearchHot>() {
        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemSearchHotBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun convert(holder: ViewBindingHolder, entity: SearchHot, position: Int) {
            holder.getViewBinding<ItemSearchHotBinding>().apply {
                tvHot.text = entity.key
                tvHot.setPressTextColor(CommonTools.getRandomColor())
                tvHot.setNormalTextColor(CommonTools.getRandomColor())
            }
        }

    }
}