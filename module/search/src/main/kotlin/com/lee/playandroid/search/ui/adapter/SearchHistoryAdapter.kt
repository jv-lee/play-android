package com.lee.playandroid.search.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.playandroid.library.common.entity.SearchHistory
import com.lee.playandroid.search.databinding.ItemSearchHistoryBinding

/**
 * @author jv.lee
 * @date 2021/11/19
 * @description
 */
class SearchHistoryAdapter(context: Context, data: List<SearchHistory>) :
    ViewBindingAdapter<SearchHistory>(context, data) {

    init {
        addItemStyles(SearchHistoryItem())
    }

    inner class SearchHistoryItem : ViewBindingItem<SearchHistory>() {
        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemSearchHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun convert(holder: ViewBindingHolder, entity: SearchHistory, position: Int) {
            holder.getViewBinding<ItemSearchHistoryBinding>().apply {
                tvText.text = entity.key
            }
        }

    }
}