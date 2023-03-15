package com.lee.playandroid.me.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.playandroid.base.adapter.binding.ViewBindingAdapter
import com.lee.playandroid.base.adapter.binding.ViewBindingHolder
import com.lee.playandroid.base.adapter.item.ViewBindingItem
import com.lee.playandroid.common.entity.CoinRecord
import com.lee.playandroid.me.databinding.ItemCoinRecordBinding

/**
 * 积分获取记录列表适配器
 * @author jv.lee
 * @date 2021/11/30
 */
class CoinRecordAdapter(context: Context) :
    ViewBindingAdapter<CoinRecord>(context) {

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