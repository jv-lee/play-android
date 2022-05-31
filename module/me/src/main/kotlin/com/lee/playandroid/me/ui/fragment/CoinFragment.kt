package com.lee.playandroid.me.ui.fragment

import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.adapter.extensions.bindAllListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.inflate
import com.lee.library.interadp.setClickListener
import com.lee.library.tools.DarkModeTools
import com.lee.library.tools.StatusTools.setDarkStatusIcon
import com.lee.library.tools.StatusTools.setLightStatusIcon
import com.lee.library.viewstate.LoadStatus
import com.lee.library.viewstate.collectState
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.common.entity.AccountViewState
import com.lee.playandroid.library.common.entity.CoinRecord
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentCoinBinding
import com.lee.playandroid.me.databinding.LayoutCoinHeaderBinding
import com.lee.playandroid.me.ui.adapter.CoinRecordAdapter
import com.lee.playandroid.me.ui.widget.CoinLoadResource
import com.lee.playandroid.me.viewmodel.CoinViewAction
import com.lee.playandroid.me.viewmodel.CoinViewModel
import com.lee.playandroid.router.navigateDetails

/**
 * 积分页面
 * @author jv.lee
 * @date 2021/11/25
 */
class CoinFragment : BaseNavigationFragment(R.layout.fragment_coin),
    BaseViewAdapter.LoadErrorListener,
    BaseViewAdapter.AutoLoadMoreListener {

    private val viewModel by viewModels<CoinViewModel>()

    private val binding by binding(FragmentCoinBinding::bind)

    private val headerBinding by inflate(LayoutCoinHeaderBinding::inflate)

    private lateinit var mAdapter: CoinRecordAdapter

    override fun bindView() {
        binding.toolbar.setClickListener {
            moreClick {
                findNavController().navigateDetails(
                    getString(R.string.coin_help_title),
                    ApiConstants.URI_COIN_HELP
                )
            }
        }
        headerBinding.tvRankLabel.setOnClickListener {
            findNavController().navigate(R.id.action_coin_fragment_to_coin_rank_fragment)
        }

        if (binding.rvContainer.adapter == null) {
            binding.rvContainer.adapter = CoinRecordAdapter(requireContext(), arrayListOf()).apply {
                mAdapter = this
                setLoadResource(CoinLoadResource())
                initStatusView()
                addHeader(headerBinding.root)
                pageLoading()
                bindAllListener(this@CoinFragment)
            }.proxy
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchWhenResumed {
            viewModel.coinRecordFlow.collectState<PageData<CoinRecord>>(success = {
                mAdapter.submitData(it, diff = true)
            }, error = {
                mAdapter.submitFailed()
                actionFailed(it)
            })
        }

        launchWhenResumed {
            viewModel.accountService.getAccountViewStates(requireActivity())
                .collectState(AccountViewState::accountData) {
                    headerBinding.tvIntegralCount.text = it?.coinInfo?.coinCount.toString()
                }
        }
    }

    override fun autoLoadMore() {
        viewModel.dispatch(CoinViewAction.RequestPage(LoadStatus.LOAD_MORE))
    }

    override fun pageReload() {
        viewModel.dispatch(CoinViewAction.RequestPage(LoadStatus.REFRESH))
    }

    override fun itemReload() {
        viewModel.dispatch(CoinViewAction.RequestPage(LoadStatus.RELOAD))
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        if (!DarkModeTools.get().isDarkTheme()) {
            requireActivity().setLightStatusIcon()
        }
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        if (!DarkModeTools.get().isDarkTheme()) {
            requireActivity().setDarkStatusIcon()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvContainer.adapter = null
    }

}