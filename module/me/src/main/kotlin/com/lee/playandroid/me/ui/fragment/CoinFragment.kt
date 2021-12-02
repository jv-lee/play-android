package com.lee.playandroid.me.ui.fragment

import androidx.fragment.app.viewModels
import com.lee.library.adapter.listener.LoadErrorListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseFragment
import com.lee.library.base.BaseNavigationAnimationFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.inflate
import com.lee.library.extensions.toast
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.observeState
import com.lee.library.tools.StatusTools
import com.lee.playandroid.library.common.entity.Coin
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.ui.widget.SimpleLoadResource
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentCoinBinding
import com.lee.playandroid.me.databinding.LayoutCoinHeaderBinding
import com.lee.playandroid.me.ui.adapter.CoinRecordAdapter
import com.lee.playandroid.me.viewmodel.CoinViewModel

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
    }

    override fun bindData() {
        viewModel.coinRecordLive.observeState<PageData<Coin>>(this, success = {
            mAdapter.submitData(it)
        }, error = {
            toast(it.message)
            mAdapter.submitFailed()
        })
    }

    override fun onResume() {
        super.onResume()
        StatusTools.setLightStatusIcon(requireActivity())
    }

    override fun onStop() {
        super.onStop()
        StatusTools.setDarkStatusIcon(requireActivity())
    }

}