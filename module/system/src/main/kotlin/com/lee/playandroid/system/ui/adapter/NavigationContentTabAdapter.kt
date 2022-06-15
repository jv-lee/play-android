package com.lee.playandroid.system.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.playandroid.base.adapter.core.VerticalTabAdapter
import com.lee.playandroid.base.extensions.setBackgroundDrawableCompat
import com.lee.playandroid.base.extensions.setTextColorCompat
import com.lee.playandroid.common.entity.NavigationItem
import com.lee.playandroid.system.R
import com.lee.playandroid.system.databinding.ItemNavigationTabBinding

/**
 * 导航内容左侧tab适配器
 * @author jv.lee
 * @date 2021/11/12
 */
class NavigationContentTabAdapter(data: MutableList<NavigationItem>) :
    VerticalTabAdapter<NavigationItem>(data) {

    override fun createViewBinding(context: Context, parent: ViewGroup): ViewBinding {
        return ItemNavigationTabBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun convert(holder: VerticalTabViewHolder, entity: NavigationItem, position: Int) {
        holder.getViewBinding<ItemNavigationTabBinding>().apply {
            tvText.text = entity.name
            if (selectIndex == position) {
                tvText.setTextColorCompat(R.color.colorThemeFocusLight)
                tvText.setBackgroundDrawableCompat(R.drawable.shape_selected_tab)
            } else {
                tvText.setTextColorCompat(R.color.colorThemePrimary)
                tvText.background = null
            }
        }
    }

}