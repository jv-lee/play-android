package com.lee.playandroid.me.ui.fragment

import androidx.fragment.app.viewModels
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentCoinRankBinding
import com.lee.playandroid.me.viewmodel.CoinRankViewModel

/**
 * @author jv.lee
 * @date 2021/12/8
 * @description 积分排行榜页面
 */
class CoinRankFragment : BaseFragment(R.layout.fragment_coin_rank) {

    private val viewModel by viewModels<CoinRankViewModel>()

    private val binding by binding(FragmentCoinRankBinding::bind)

    override fun bindView() {

    }

    override fun bindData() {

    }

}