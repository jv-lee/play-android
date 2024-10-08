package com.lee.playandroid.system.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import com.lee.playandroid.base.adapter.binding.ViewBindingAdapter
import com.lee.playandroid.base.adapter.binding.ViewBindingHolder
import com.lee.playandroid.base.adapter.item.ViewBindingItem
import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.entity.NavigationItem
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.system.R
import com.lee.playandroid.system.databinding.ItemNavigationContentBinding

/**
 * 导航内容列表适配器
 * @author jv.lee
 * @date 2021/11/16
 */
class NavigationContentAdapter(context: Context) :
    ViewBindingAdapter<NavigationItem>(context) {

    init {
        addItemStyles(NavigationContentItem())
    }

    inner class NavigationContentItem :
        ViewBindingItem<NavigationItem>() {

        override fun convert(
            holder: ViewBindingHolder,
            entity: NavigationItem,
            position: Int
        ) {
            val context = holder.itemView.context

            holder.getViewBinding<ItemNavigationContentBinding>().apply {
                tvTitle.text = entity.name

                // 构建适配器
                rvContainer.adapter = NavigationContentTagAdapter(context).apply {
                    addData(entity.articles)
                    setOnItemChildClickListener(object : OnItemChildView<Content> {
                        override fun onItemChildClick(view: View, entity: Content, position: Int) {
                            Navigation.findNavController(view)
                                .navigateDetails(
                                    entity.title,
                                    entity.link,
                                    entity.id,
                                    entity.collect
                                )
                        }
                    }, R.id.tv_tag)
                }
            }
        }

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemNavigationContentBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        }
    }
}