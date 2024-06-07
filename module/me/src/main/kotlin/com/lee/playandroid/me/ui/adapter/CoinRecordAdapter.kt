package com.lee.playandroid.me.ui.adapter

import android.content.Context
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

    inner class CoinRecordItem : ViewBindingItem<ItemCoinRecordBinding, CoinRecord>() {

        override fun ItemCoinRecordBinding.convert(
            holder: ViewBindingHolder,
            entity: CoinRecord,
            position: Int
        ) {
            tvText.text = entity.desc
        }
    }
}