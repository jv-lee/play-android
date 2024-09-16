package com.lee.playandroid.me.ui.fragment

import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.base.adapter.base.BaseViewAdapter
import com.lee.playandroid.base.adapter.extensions.bindAllListener
import com.lee.playandroid.base.adapter.page.submitData
import com.lee.playandroid.base.adapter.page.submitFailed
import com.lee.playandroid.base.base.BaseBindingNavigationFragment
import com.lee.playandroid.base.extensions.collectState
import com.lee.playandroid.base.interadp.setClickListener
import com.lee.playandroid.base.tools.DarkModeTools
import com.lee.playandroid.base.tools.SystemBarTools.setDarkStatusIcon
import com.lee.playandroid.base.tools.SystemBarTools.setLightStatusIcon
import com.lee.playandroid.base.uistate.LoadStatus
import com.lee.playandroid.base.uistate.collectCallback
import com.lee.playandroid.common.constants.ApiConstants
import com.lee.playandroid.common.entity.AccountViewState
import com.lee.playandroid.common.entity.CoinRecord
import com.lee.playandroid.common.entity.PageData
import com.lee.playandroid.common.extensions.actionFailed
import com.lee.playandroid.common.ui.adapter.SimpleTextAdapter
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentCoinBinding
import com.lee.playandroid.me.ui.adapter.CoinRecordAdapter
import com.lee.playandroid.me.ui.widget.CoinLoadResource
import com.lee.playandroid.me.viewmodel.CoinViewIntent
import com.lee.playandroid.me.viewmodel.CoinViewModel
import com.lee.playandroid.router.navigateDetails

/**
 * 积分页面
 * @author jv.lee
 * @date 2021/11/25
 */
class CoinFragment :
    BaseBindingNavigationFragment<FragmentCoinBinding>(),
    BaseViewAdapter.LoadErrorListener,
    BaseViewAdapter.AutoLoadMoreListener {

    private val viewModel by viewModels<CoinViewModel>()

    private lateinit var mAdapter: CoinRecordAdapter

    override fun bindView() {
        mBinding.toolbar.setClickListener {
            moreClick {
                findNavController().navigateDetails(
                    getString(R.string.coin_help_title),
                    ApiConstants.URI_COIN_HELP
                )
            }
        }
        mBinding.layoutCoinHeader.tvRankLabel.setOnClickListener {
            findNavController().navigate(R.id.action_coin_fragment_to_coin_rank_fragment)
        }

        if (mBinding.rvContainer.adapter == null) {
            CoinRecordAdapter(requireContext()).apply {
                mAdapter = this
                bindRecyclerView(mBinding.rvContainer, CoinLoadResource())
                bindAllListener(this@CoinFragment)
            }
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchWhenResumed {
            viewModel.coinRecordFlow.collectCallback<PageData<CoinRecord>>(
                success = {
                    mAdapter.submitData(it, diff = true)
                },
                error = {
                    mAdapter.submitFailed()
                    actionFailed(it)
                }
            )
        }

        launchWhenResumed {
            viewModel.accountService.getAccountViewStates(requireActivity())
                .collectState(AccountViewState::accountData) {
                    mBinding.layoutCoinHeader.tvIntegralCount.text =
                        it?.coinInfo?.coinCount.toString()
                }
        }
    }

    override fun autoLoadMore() {
        viewModel.dispatch(CoinViewIntent.RequestPage(LoadStatus.LOAD_MORE))
    }

    override fun pageReload() {
        viewModel.dispatch(CoinViewIntent.RequestPage(LoadStatus.REFRESH))
    }

    override fun itemReload() {
        viewModel.dispatch(CoinViewIntent.RequestPage(LoadStatus.RELOAD))
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        if (!DarkModeTools.get().isDarkTheme()) {
            requireActivity().window.setLightStatusIcon()
        }
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        if (!DarkModeTools.get().isDarkTheme()) {
            requireActivity().window.setDarkStatusIcon()
        }
    }

    override fun onDestroyView() {
        mBinding.rvContainer.adapter = null
        super.onDestroyView()
    }
}