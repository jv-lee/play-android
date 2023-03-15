package com.lee.playandroid.system.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.viewbinding.ViewBinding
import com.lee.playandroid.base.adapter.binding.ViewBindingAdapter
import com.lee.playandroid.base.adapter.binding.ViewBindingHolder
import com.lee.playandroid.base.adapter.item.ViewBindingItem
import com.lee.playandroid.common.entity.ParentTab
import com.lee.playandroid.common.entity.Tab
import com.lee.playandroid.system.databinding.ItemSystemContentBinding

/**
 * 体系列表适配器
 * @author jv.lee
 * @date 2021/11/10
 */
class SystemContentAdapter(context: Context) :
    ViewBindingAdapter<ParentTab>(context) {

    init {
        addItemStyles(SystemContentItem())
    }

    inner class SystemContentItem : ViewBindingItem<ParentTab>() {
        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemSystemContentBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun convert(holder: ViewBindingHolder, entity: ParentTab, position: Int) {
            holder.getViewBinding<ItemSystemContentBinding>().apply {
                tvTitle.text = entity.name

                tvChildLabel.text = HtmlCompat.fromHtml(
                    buildChildrenLabel(entity.children),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }

        private fun buildChildrenLabel(tabs: List<Tab>): String {
            val builder = StringBuilder()
            tabs.forEach { builder.append(it.name + "\t\t") }
            return builder.toString()
        }
    }
}