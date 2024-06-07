package com.lee.playandroid.official.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.playandroid.base.adapter.binding.ViewBindingAdapter
import com.lee.playandroid.base.adapter.binding.ViewBindingHolder
import com.lee.playandroid.base.adapter.item.ViewBindingItem
import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.extensions.getAuthor
import com.lee.playandroid.common.extensions.getCategory
import com.lee.playandroid.common.extensions.getDateFormat
import com.lee.playandroid.common.extensions.getTitle
import com.lee.playandroid.official.databinding.ItemOfficialBinding

/**
 * 公众号列表适配器
 * @author jv.lee
 * @date 2021/11/8
 */
class OfficialListAdapter(context: Context) :
    ViewBindingAdapter<Content>(context) {

    init {
        addItemStyles(OfficialItem())
    }

    inner class OfficialItem : ViewBindingItem<ItemOfficialBinding, Content>() {

        override fun ItemOfficialBinding.convert(
            holder: ViewBindingHolder,
            entity: Content,
            position: Int
        ) {
            entity.apply {
                tvTitle.text = getTitle()
                tvAuthor.text = getAuthor()
                tvTime.text = getDateFormat()
                tvCategory.text = getCategory()
            }
        }
    }
}