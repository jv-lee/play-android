package com.lee.playandroid.system.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.library.utils.LogUtil
import com.lee.playandroid.library.common.entity.NavigationItem
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.system.databinding.ItemNavigationContentBinding

/**
 * @author jv.lee
 * @data 2021/11/16
 * @description
 */
class NavigationContentAdapter(context: Context, data: List<NavigationItem>) :
    ViewBindingAdapter<NavigationItem>(context, data) {

    init {
        addItemStyles(NavigationContentViewHolder())
    }

    inner class NavigationContentViewHolder : ViewBindingItem<NavigationItem>() {

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemNavigationContentBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun convert(holder: ViewBindingHolder, entity: NavigationItem, position: Int) {
            holder.getViewBinding<ItemNavigationContentBinding>().apply {
                val context = holder.itemView.context
                tvTitle.text = entity.name

                //构建适配器
                rvContainer.adapter = NavigationContentTagAdapter(context, entity.articles).apply {
                    setOnItemClickListener { view, entity, _ ->
                        LogUtil.i(entity.link)
                        Navigation.findNavController(view).navigateDetails(entity.link)
                    }
                }
            }
        }

    }
}