package com.lee.playandroid.system.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.core.VerticalTabAdapter
import com.lee.library.extensions.setBackgroundDrawableCompat
import com.lee.library.extensions.setTextColorCompat
import com.lee.playandroid.library.common.entity.NavigationItem
import com.lee.playandroid.system.R
import com.lee.playandroid.system.databinding.ItemNavigationTabBinding

/**
 * @author jv.lee
 * @data 2021/11/12
 * @description
 */
class NavigationTabAdapter(data: MutableList<NavigationItem>) :
    VerticalTabAdapter<NavigationItem>(data) {

    override fun createViewBinding(context: Context, parent: ViewGroup): ViewBinding {
        return ItemNavigationTabBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun convert(holder: VerticalTabViewHolder, entity: NavigationItem, position: Int) {
        holder.getViewBinding<ItemNavigationTabBinding>().apply {
            tvText.text = entity.name
            if (selectIndex == position) {
                tvText.setTextColorCompat(R.color.colorFocusDark)
                tvText.setBackgroundDrawableCompat(R.drawable.shape_selected_tab)
            } else {
                tvText.setTextColorCompat(R.color.colorThemePrimary)
                tvText.background = null
            }
        }
    }

}