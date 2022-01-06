package com.lee.playandroid.square.ui.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.listener.LoadErrorListener
import com.lee.library.adapter.page.submitData
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.delayBackEvent
import com.lee.library.extensions.smoothScrollToTop
import com.lee.library.livedatabus.InjectBus
import com.lee.library.livedatabus.LiveDataBus
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.observeState
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.NavigationSelectEvent
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.library.common.ui.extensions.setThemeGradientBackground
import com.lee.playandroid.library.common.ui.widget.OffsetItemDecoration
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.square.R
import com.lee.playandroid.square.databinding.FragmentSquareBinding
import com.lee.playandroid.square.ui.adapter.SquareAdapter
import com.lee.playandroid.square.viewmodel.SquareViewModel

/**
 * @author jv.lee
 * @date 2021/12/13
 * @description 首页第二个Tab 广场页面
 */
class SquareFragment : BaseFragment(R.layout.fragment_square) {

    private val viewModel by viewModels<SquareViewModel>()

    private val binding by binding(FragmentSquareBinding::bind)

    private lateinit var mAdapter: SquareAdapter

    override fun bindView() {
        delayBackEvent()

        binding.toolbar.setThemeGradientBackground()
        binding.ivCreate.setOnClickListener {
            findNavController().navigate(R.id.action_square_fragment_to_create_share_fragment)
        }

        binding.rvContainer.addItemDecoration(OffsetItemDecoration(binding.toolbar.getToolbarLayoutHeight()))
        binding.rvContainer.adapter = SquareAdapter(requireContext(), arrayListOf()).apply {
            mAdapter = this
            initStatusView()
            pageLoading()
            setAutoLoadMoreListener {
                viewModel.requestSquareData(LoadStatus.LOAD_MORE)
            }
            setLoadErrorListener(object : LoadErrorListener {
                override fun pageReload() {
                    viewModel.requestSquareData(LoadStatus.REFRESH)
                }

                override fun itemReload() {
                    viewModel.requestSquareData(LoadStatus.RELOAD)
                }
            })

            setOnItemClickListener { _, entity, _ ->
                entity?.apply {
                    findNavController().navigateDetails(title, link, id, collect)
                }
            }

        }.proxy

        binding.refreshView.setOnRefreshListener {
            mAdapter.openLoadMore()
            viewModel.requestSquareData(LoadStatus.REFRESH)
        }
    }

    override fun bindData() {
        LiveDataBus.getInstance().injectBus(this)

        viewModel.squareLive.observeState<PageData<Content>>(this, success = {
            binding.refreshView.isRefreshing = false
            mAdapter.submitData(it)
        }, error = {
            binding.refreshView.isRefreshing = false
            mAdapter.loadFailed()
            actionFailed(it)
        })
    }

    @InjectBus(NavigationSelectEvent.key, isActive = true)
    fun navigationEvent(event: NavigationSelectEvent) {
        if (event.title == getString(R.string.nav_square) && isResumed) {
            binding.rvContainer.smoothScrollToTop()
        }
    }

}