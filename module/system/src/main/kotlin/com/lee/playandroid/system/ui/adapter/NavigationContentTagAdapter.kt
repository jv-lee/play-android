package com.lee.playandroid.system.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
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
class NavigationContentTagAdapter(context: Context, data: List<Content>) :
    ViewBindingAdapter<Content>(context, data) {

    init {
        addItemStyles(NavigationContentTagItem())
    }

    inner class NavigationContentTagItem : ViewBindingItem<Content>() {
        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemNavigationContentTagBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        }

        override fun convert(holder: ViewBindingHolder, entity: Content, position: Int) {
            holder.getViewBinding<ItemNavigationContentTagBinding>().apply {
                tvTag.text = entity.getTitle()
            }
        }
    }

}