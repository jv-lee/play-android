package com.lee.playandroid.me.ui.fragment

import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.lee.playandroid.base.adapter.base.BaseViewAdapter
import com.lee.playandroid.base.adapter.extensions.bindAllListener
import com.lee.playandroid.base.adapter.page.submitData
import com.lee.playandroid.base.adapter.page.submitFailed
import com.lee.playandroid.base.base.BaseNavigationFragment
import com.lee.playandroid.base.extensions.binding
import com.lee.playandroid.base.interadp.setClickListener
import com.lee.playandroid.base.uistate.LoadStatus
import com.lee.playandroid.base.uistate.collectState
import com.lee.playandroid.common.constants.ApiConstants
import com.lee.playandroid.common.entity.CoinRank
import com.lee.playandroid.common.entity.PageData
import com.lee.playandroid.common.extensions.actionFailed
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentCoinRankBinding
import com.lee.playandroid.me.ui.adapter.CoinRankAdapter
import com.lee.playandroid.me.ui.widget.RankSpanSizeLookup
import com.lee.playandroid.me.viewmodel.CoinRankViewIntent
import com.lee.playandroid.me.viewmodel.CoinRankViewModel
import com.lee.playandroid.router.navigateDetails

/**
 * 积分排行榜页面
 * @author jv.lee
 * @date 2021/12/8
 */
class CoinRankFragment :
    BaseNavigationFragment(R.layout.fragment_coin_rank),
    BaseViewAdapter.LoadErrorListener,
    BaseViewAdapter.AutoLoadMoreListener {

    private val viewModel by viewModels<CoinRankViewModel>()

    private val binding by binding(FragmentCoinRankBinding::bind)

    private lateinit var mAdapter: CoinRankAdapter

    override fun bindView() {
        binding.toolbar.setClickListener {
            moreClick {
                findNavController().navigateDetails(
                    getString(R.string.coin_help_title),
                    ApiConstants.URI_COIN_HELP
                )
            }
        }

        binding.rvContainer.layoutManager = GridLayoutManager(requireContext(), 3).apply {
            spanSizeLookup = RankSpanSizeLookup()
        }
        if (binding.rvContainer.adapter == null) {
            binding.rvContainer.adapter = CoinRankAdapter(requireContext()).apply {
                mAdapter = this
                initStatusView()
                pageLoading()
                bindAllListener(this@CoinRankFragment)
            }.getProxy()
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchWhenResumed {
            viewModel.coinRankFlow.collectState<PageData<CoinRank>>(
                success = {
                    mAdapter.submitData(it, diff = true)
                },
                error = {
                    mAdapter.submitFailed()
                    actionFailed(it)
                }
            )
        }
    }

    override fun autoLoadMore() {
        viewModel.dispatch(CoinRankViewIntent.RequestPage(LoadStatus.LOAD_MORE))
    }

    override fun pageReload() {
        viewModel.dispatch(CoinRankViewIntent.RequestPage(LoadStatus.REFRESH))
    }

    override fun itemReload() {
        viewModel.dispatch(CoinRankViewIntent.RequestPage(LoadStatus.RELOAD))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvContainer.adapter = null
    }
}