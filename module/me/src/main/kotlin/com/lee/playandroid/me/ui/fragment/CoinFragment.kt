package com.lee.playandroid.me.ui.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.listener.LoadErrorListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.inflate
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.observeState
import com.lee.library.tools.DarkModeTools
import com.lee.library.tools.StatusTools
import com.lee.library.widget.toolbar.TitleToolbar
import com.lee.playandroid.library.common.constants.ApiConstants
import com.lee.playandroid.library.common.entity.CoinRecord
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.library.common.ui.widget.SimpleLoadResource
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentCoinBinding
import com.lee.playandroid.me.databinding.LayoutCoinHeaderBinding
import com.lee.playandroid.me.ui.adapter.CoinRecordAdapter
import com.lee.playandroid.me.viewmodel.CoinViewModel
import com.lee.playandroid.router.navigateDetails

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description 积分页面
 */
class CoinFragment : BaseFragment(R.layout.fragment_coin) {

    private val accountService = ModuleService.find<AccountService>()

    private val viewModel by viewModels<CoinViewModel>()

    private val binding by binding(FragmentCoinBinding::bind)

    private val headerBinding by inflate(LayoutCoinHeaderBinding::inflate)

    private lateinit var mAdapter: CoinRecordAdapter

    override fun bindView() {
        accountService.getAccountInfo(requireActivity())?.apply {
            headerBinding.tvIntegralCount.text = coinInfo.coinCount.toString()
        }

        binding.rvContainer.adapter = CoinRecordAdapter(requireContext(), arrayListOf()).apply {
            mAdapter = this
            setLoadResource(SimpleLoadResource())
            initStatusView()
            addHeader(headerBinding.root)
            pageLoading()

            setAutoLoadMoreListener {
                viewModel.requestCoinRecord(LoadStatus.LOAD_MORE)
            }

            setLoadErrorListener(object : LoadErrorListener {
                override fun pageReload() {
                    viewModel.requestCoinRecord(LoadStatus.REFRESH)
                }

                override fun itemReload() {
                    viewModel.requestCoinRecord(LoadStatus.RELOAD)
                }
            })

        }.proxy

        binding.toolbar.setClickListener(object : TitleToolbar.ClickListener() {
            override fun moreClick() {
                findNavController().navigateDetails(
                    getString(R.string.coin_help_title),
                    ApiConstants.URI_COIN_HELP
                )
            }
        })
        headerBinding.tvRankLabel.setOnClickListener {
            findNavController().navigate(R.id.action_coin_fragment_to_coin_rank_fragment)
        }
    }

    override fun bindData() {
        viewModel.coinRecordLive.observeState<PageData<CoinRecord>>(this, success = {
            mAdapter.submitData(it, diff = true)
        }, error = {
            mAdapter.submitFailed()
            actionFailed(it)
        })
    }

    override fun onResume() {
        super.onResume()
        if (!DarkModeTools.get().isDarkTheme()) {
            StatusTools.setLightStatusIcon(requireActivity())
        }
    }

    override fun onStop() {
        super.onStop()
        if (!DarkModeTools.get().isDarkTheme()) {
            StatusTools.setDarkStatusIcon(requireActivity())
        }
    }

}