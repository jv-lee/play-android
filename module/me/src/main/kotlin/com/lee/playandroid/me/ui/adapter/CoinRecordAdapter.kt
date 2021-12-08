package com.lee.playandroid.me.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.playandroid.library.common.entity.CoinRecord
import com.lee.playandroid.me.databinding.ItemCoinRecordBinding

/**
 * @author jv.lee
 * @date 2021/11/30
 * @description 积分获取记录列表适配器
 */
class CoinRecordAdapter(context: Context, data: List<CoinRecord>) :
    ViewBindingAdapter<CoinRecord>(context, data) {

    init {
        addItemStyles(CoinRecordItem())
    }

    inner class CoinRecordItem : ViewBindingItem<CoinRecord>() {
        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemCoinRecordBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun convert(holder: ViewBindingHolder, entity: CoinRecord, position: Int) {
            holder.getViewBinding<ItemCoinRecordBinding>().apply {
                tvText.text = entity.desc
            }
        }

    }

}