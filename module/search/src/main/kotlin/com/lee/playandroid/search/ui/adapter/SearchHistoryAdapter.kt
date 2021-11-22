package com.lee.playandroid.search.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.library.extensions.setImageTintCompat
import com.lee.playandroid.library.common.entity.SearchHistory
import com.lee.playandroid.library.common.tools.CommonTools
import com.lee.playandroid.search.R
import com.lee.playandroid.search.databinding.ItemSearchHistoryBinding
import com.lee.playandroid.search.databinding.ItemSearchHotBinding

/**
 * @author jv.lee
 * @date 2021/11/19
 * @description
 */
class SearchHistoryAdapter(context: Context, data: List<SearchHistory>) :
    ViewBindingAdapter<SearchHistory>(context, data) {

    init {
        addItemStyles(SearchHistoryItemViewHolder())
    }

    inner class SearchHistoryItemViewHolder : ViewBindingItem<SearchHistory>() {
        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemSearchHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun convert(holder: ViewBindingHolder, entity: SearchHistory, position: Int) {
            holder.getViewBinding<ItemSearchHistoryBinding>().apply {
                tvText.text = entity.key
                ivDelete.setImageTintCompat(R.drawable.vector_close, R.color.colorThemePrimary)
            }
        }

    }
}