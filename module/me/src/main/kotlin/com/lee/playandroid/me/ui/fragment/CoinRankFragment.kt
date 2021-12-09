package com.lee.playandroid.me.ui.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.mvvm.ui.observeState
import com.lee.library.widget.toolbar.TitleToolbar
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.common.entity.CoinRank
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentCoinRankBinding
import com.lee.playandroid.me.viewmodel.CoinRankViewModel
import com.lee.playandroid.router.navigateDetails

/**
 * @author jv.lee
 * @date 2021/12/8
 * @description 积分排行榜页面
 */
class CoinRankFragment : BaseFragment(R.layout.fragment_coin_rank) {

    private val viewModel by viewModels<CoinRankViewModel>()

    private val binding by binding(FragmentCoinRankBinding::bind)

    override fun bindView() {
        binding.toolbar.setClickListener(object : TitleToolbar.ClickListener() {
            override fun menuClick() {
                findNavController().navigateDetails(
                    getString(R.string.coin_help_title),
                    ApiConstants.URI_COIN_HELP
                )
            }
        })
    }

    override fun bindData() {
        viewModel.coinRankLive.observeState<PageData<CoinRank>>(this, success = {

        }, error = {

        })
    }

}