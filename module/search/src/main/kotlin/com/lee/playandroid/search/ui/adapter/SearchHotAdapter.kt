package com.lee.playandroid.search.ui.adapter

import android.content.Context
import com.lee.playandroid.base.adapter.binding.ViewBindingAdapter
import com.lee.playandroid.base.adapter.binding.ViewBindingHolder
import com.lee.playandroid.base.adapter.item.ViewBindingItem
import com.lee.playandroid.search.databinding.ItemSearchHotBinding
import com.lee.playandroid.search.model.entity.SearchHotUI

/**
 * 搜索热词列表适配器
 * @author jv.lee
 * @date 2021/11/19
 */
class SearchHotAdapter(context: Context) :
    ViewBindingAdapter<SearchHotUI>(context) {

    init {
        addItemStyles(SearchHotItem())
    }

    inner class SearchHotItem : ViewBindingItem<ItemSearchHotBinding, SearchHotUI>() {

        override fun ItemSearchHotBinding.convert(
            holder: ViewBindingHolder,
            entity: SearchHotUI,
            position: Int
        ) {
            holder.getViewBinding<ItemSearchHotBinding>().apply {
                tvHot.text = entity.key
                tvHot.setPressTextColor(entity.pressColor)
                tvHot.setNormalTextColor(entity.normalColor)
            }
        }
    }
}