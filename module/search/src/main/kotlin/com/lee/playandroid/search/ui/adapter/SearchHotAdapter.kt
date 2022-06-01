package com.lee.playandroid.search.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.playandroid.base.adapter.binding.ViewBindingAdapter
import com.lee.playandroid.base.adapter.binding.ViewBindingHolder
import com.lee.playandroid.base.adapter.item.ViewBindingItem
import com.lee.playandroid.search.databinding.ItemSearchHotBinding
import com.lee.playandroid.search.model.entity.SearchHot
import java.util.*

/**
 *
 * @author jv.lee
 * @date 2021/11/19
 */
class SearchHotAdapter(context: Context, data: List<SearchHot>) :
    ViewBindingAdapter<SearchHot>(context, data) {

    init {
        addItemStyles(SearchHotItem())
    }

    inner class SearchHotItem : ViewBindingItem<SearchHot>() {
        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemSearchHotBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun convert(holder: ViewBindingHolder, entity: SearchHot, position: Int) {
            holder.getViewBinding<ItemSearchHotBinding>().apply {
                tvHot.text = entity.key
                tvHot.setPressTextColor(getRandomColor())
                tvHot.setNormalTextColor(getRandomColor())
            }
        }

    }

    private fun getRandomColor(): Int {
        val random = Random()
        return Color.rgb(
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
        )
    }
}