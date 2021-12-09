package com.lee.playandroid.me.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.playandroid.library.common.entity.CoinRank
import com.lee.playandroid.me.databinding.ItemCoinRankBinding
import com.lee.playandroid.me.databinding.ItemCoinRankTopBinding

/**
 * @author jv.lee
 * @date 2021/12/9
 * @description 积分排行榜适配器
 */
class CoinRankAdapter(context: Context, data: List<CoinRank>) :
    ViewBindingAdapter<CoinRank>(context, data) {

    init {
        addItemStyles(CoinRankItem())
        addItemStyles(CoinRankTopItem())
    }

    inner class CoinRankItem : ViewBindingItem<CoinRank>() {

        override fun isItemView(entity: CoinRank, position: Int): Boolean {
            return position >= 3
        }

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemCoinRankBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun convert(holder: ViewBindingHolder, entity: CoinRank, position: Int) {
            holder.getViewBinding<ItemCoinRankBinding>().apply {
                tvRankNumber.text = entity.rank
                tvUsername.text = entity.username
                tvUserCoin.text = entity.coinCount.toString()
                tvUserLevel.text = entity.level.toString()
            }
        }
    }

    inner class CoinRankTopItem : ViewBindingItem<CoinRank>() {
        override fun isItemView(entity: CoinRank, position: Int): Boolean {
            return position < 3
        }

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemCoinRankTopBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun convert(holder: ViewBindingHolder, entity: CoinRank, position: Int) {
            holder.getViewBinding<ItemCoinRankTopBinding>().apply {
                tvRankNumber.text = entity.rank
                tvUsername.text = entity.username
                tvUserCoin.text = entity.coinCount.toString()
                tvUserLevel.text = entity.level.toString()
            }
        }

    }

}