package com.lee.playandroid.system.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.playandroid.library.common.entity.NavigationItem
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.system.R
import com.lee.playandroid.system.databinding.ItemNavigationContentBinding

/**
 *
 * @author jv.lee
 * @date 2021/11/16
 */
class NavigationContentAdapter(context: Context, data: List<NavigationItem>) :
    ViewBindingAdapter<NavigationItem>(context, data) {

    init {
        addItemStyles(NavigationContentItem())
    }

    inner class NavigationContentItem : ViewBindingItem<NavigationItem>() {

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemNavigationContentBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun convert(holder: ViewBindingHolder, entity: NavigationItem, position: Int) {
            holder.getViewBinding<ItemNavigationContentBinding>().apply {
                val context = holder.itemView.context
                tvTitle.text = entity.name

                //构建适配器
                rvContainer.adapter = NavigationContentTagAdapter(context, entity.articles).apply {
                    setOnItemChildClickListener({ view, entity, _ ->
                        Navigation.findNavController(view)
                            .navigateDetails(entity.title, entity.link, entity.id, entity.collect)
                    }, R.id.tv_tag)
                }
            }
        }

    }
}