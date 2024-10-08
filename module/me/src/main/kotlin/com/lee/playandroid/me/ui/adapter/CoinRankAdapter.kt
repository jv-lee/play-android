package com.lee.playandroid.me.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.playandroid.base.adapter.binding.ViewBindingAdapter
import com.lee.playandroid.base.adapter.binding.ViewBindingHolder
import com.lee.playandroid.base.adapter.item.ViewBindingItem
import com.lee.playandroid.common.entity.CoinRank
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.ItemCoinRankBinding
import com.lee.playandroid.me.databinding.ItemCoinRankTopBinding

/**
 * 积分排行榜适配器
 * @author jv.lee
 * @date 2021/12/9
 */
class CoinRankAdapter(context: Context) :
    ViewBindingAdapter<CoinRank>(context) {

    init {
        addItemStyles(CoinRankItem())
        addItemStyles(CoinRankTopItem())
    }

    inner class CoinRankItem : ViewBindingItem<CoinRank>() {

        override fun isItemView(entity: CoinRank, position: Int): Boolean {
            return position >= 3
        }

        override fun convert(
            holder: ViewBindingHolder,
            entity: CoinRank,
            position: Int
        ) {
            holder.getViewBinding<ItemCoinRankBinding>().apply {
                tvRankNumber.text = entity.rank
                tvUsername.text = entity.username
                tvUserCoin.text = entity.coinCount.toString()
                tvUserLevel.text = entity.level.toString()
            }
        }

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemCoinRankBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        }
    }

    inner class CoinRankTopItem : ViewBindingItem<CoinRank>() {
        override fun isItemView(entity: CoinRank, position: Int): Boolean {
            return position < 3
        }

        override fun convert(
            holder: ViewBindingHolder,
            entity: CoinRank,
            position: Int
        ) {
            holder.getViewBinding<ItemCoinRankTopBinding>().apply {
                tvUsername.text = entity.username
                tvUserCoin.text = entity.coinCount.toString()

                ivRankIcon.setImageResource(
                    when (entity.rank) {
                        "1" -> R.drawable.vector_rank_no1
                        "2" -> R.drawable.vector_rank_no2
                        "3" -> R.drawable.vector_rank_no3
                        else -> 0
                    }
                )
            }
        }

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemCoinRankTopBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        }
    }
}