package com.lee.playandroid.search.ui.adapter

import android.content.Context
import com.lee.playandroid.base.adapter.binding.ViewBindingAdapter
import com.lee.playandroid.base.adapter.binding.ViewBindingHolder
import com.lee.playandroid.base.adapter.item.ViewBindingItem
import com.lee.playandroid.common.entity.SearchHistory
import com.lee.playandroid.search.databinding.ItemSearchHistoryBinding

/**
 * 搜索历史记录列表适配器
 * @author jv.lee
 * @date 2021/11/19
 */
class SearchHistoryAdapter(context: Context) :
    ViewBindingAdapter<SearchHistory>(context) {

    init {
        addItemStyles(SearchHistoryItem())
    }

    inner class SearchHistoryItem : ViewBindingItem<ItemSearchHistoryBinding, SearchHistory>() {

        override fun ItemSearchHistoryBinding.convert(
            holder: ViewBindingHolder,
            entity: SearchHistory,
            position: Int
        ) {
            tvText.text = entity.key
        }
    }
}