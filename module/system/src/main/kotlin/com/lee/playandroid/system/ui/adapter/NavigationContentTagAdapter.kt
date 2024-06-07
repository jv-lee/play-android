package com.lee.playandroid.system.ui.adapter

import android.content.Context
import com.lee.playandroid.base.adapter.binding.ViewBindingAdapter
import com.lee.playandroid.base.adapter.binding.ViewBindingHolder
import com.lee.playandroid.base.adapter.item.ViewBindingItem
import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.extensions.getTitle
import com.lee.playandroid.system.databinding.ItemNavigationContentTagBinding

/**
 * 导航内容右侧tag适配器
 * @author jv.lee
 * @date 2021/11/16
 */
class NavigationContentTagAdapter(context: Context) :
    ViewBindingAdapter<Content>(context) {

    init {
        addItemStyles(NavigationContentTagItem())
    }

    inner class NavigationContentTagItem :
        ViewBindingItem<ItemNavigationContentTagBinding, Content>() {

        override fun ItemNavigationContentTagBinding.convert(
            holder: ViewBindingHolder,
            entity: Content,
            position: Int
        ) {
            tvTag.text = entity.getTitle()
        }
    }
}