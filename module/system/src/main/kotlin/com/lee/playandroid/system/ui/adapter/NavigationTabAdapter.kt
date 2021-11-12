package com.lee.playandroid.system.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.playandroid.library.common.entity.NavigationItem
import com.lee.playandroid.system.databinding.ItemNavigationBinding

/**
 * @author jv.lee
 * @data 2021/11/12
 * @description
 */
class NavigationTabAdapter(context: Context, data: List<NavigationItem>) :
    ViewBindingAdapter<NavigationItem>(context, data) {

    private var selectIndex = 0

    init {
        addItemStyles(NavigationAdapterItem())
    }

    inner class NavigationAdapterItem : ViewBindingItem<NavigationItem>() {
        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemNavigationBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun convert(holder: ViewBindingHolder, entity: NavigationItem, position: Int) {
            holder.getViewBinding<ItemNavigationBinding>().apply {
                tvText.text = entity.name
                if (selectIndex == position) {
                    root.setBackgroundColor(Color.GRAY)
                } else {
                    root.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }
    }

    fun selectItem(position: Int) {
        val oldPosition = selectIndex
        selectIndex = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(selectIndex)
    }


}