package com.lee.playandroid.me.ui.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.adapter.extensions.bindAllListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.observeState
import com.lee.library.widget.toolbar.TitleToolbar
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.common.entity.CoinRank
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentCoinRankBinding
import com.lee.playandroid.me.ui.adapter.CoinRankAdapter
import com.lee.playandroid.me.ui.widget.RankSpanSizeLookup
import com.lee.playandroid.me.viewmodel.CoinRankViewModel
import com.lee.playandroid.router.navigateDetails

/**
 * @author jv.lee
 * @date 2021/12/8
 * @description 积分排行榜页面
 */
class CoinRankFragment : BaseFragment(R.layout.fragment_coin_rank),
    BaseViewAdapter.LoadErrorListener,
    BaseViewAdapter.AutoLoadMoreListener {

    private val viewModel by viewModels<CoinRankViewModel>()

    private val binding by binding(FragmentCoinRankBinding::bind)

    private lateinit var mAdapter: CoinRankAdapter

    override fun bindView() {
        binding.toolbar.setClickListener(
            object : TitleToolbar.ClickListener() {
                override fun moreClick() {
                    findNavController().navigateDetails(
                        getString(R.string.coin_help_title),
                        ApiConstants.URI_COIN_HELP
                    )
                }
            })

        binding.rvContainer.layoutManager = GridLayoutManager(requireContext(), 3).apply {
            spanSizeLookup = RankSpanSizeLookup()
        }
        if (binding.rvContainer.adapter == null) {
            binding.rvContainer.adapter = CoinRankAdapter(requireContext(), arrayListOf()).apply {
                mAdapter = this
                initStatusView()
                pageLoading()
                bindAllListener(this@CoinRankFragment)
            }.proxy
        }
    }

    override fun bindData() {
        viewModel.coinRankLive.observeState<PageData<CoinRank>>(this, success = {
            mAdapter.submitData(it, diff = true)
        }, error = {
            mAdapter.submitFailed()
            actionFailed(it)
        })
    }

    override fun autoLoadMore() {
        viewModel.requestCoinRank(LoadStatus.LOAD_MORE)
    }

    override fun pageReload() {
        viewModel.requestCoinRank(LoadStatus.REFRESH)
    }

    override fun itemReload() {
        viewModel.requestCoinRank(LoadStatus.RELOAD)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvContainer.adapter = null
    }

}