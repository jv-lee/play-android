package com.lee.playandroid.home.view

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.listener.LoadErrorListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.delayBackEvent
import com.lee.library.extensions.smoothScrollToTop
import com.lee.library.livedatabus.InjectBus
import com.lee.library.livedatabus.LiveDataBus
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.observeState
import com.lee.library.widget.banner.BannerView
import com.lee.playandroid.home.R
import com.lee.playandroid.home.bean.HomeContent
import com.lee.playandroid.home.databinding.FragmentHomeBinding
import com.lee.playandroid.home.view.adapter.ContentAdapter
import com.lee.playandroid.home.viewmodel.HomeViewModel
import com.lee.playandroid.library.common.entity.NavigationSelectEvent
import com.lee.playandroid.library.common.entity.PageUiData
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.library.common.ui.extensions.setThemeGradientBackground
import com.lee.playandroid.library.common.ui.widget.OffsetItemDecoration
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.router.navigateSearch

/**
 * @author jv.lee
 * @date 2021/11/2
 * @description 首页第一个Tab 主页
 */
class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val viewModel by viewModels<HomeViewModel>()

    private val binding by binding(FragmentHomeBinding::bind)

    private lateinit var mAdapter: ContentAdapter

    override fun bindView() {
        delayBackEvent()

        binding.toolbar.setThemeGradientBackground()
        binding.ivSearch.setOnClickListener {
            findNavController().navigateSearch()
        }

        binding.rvContainer.addSaveStateViewType(BannerView::class.java)
        binding.rvContainer.addItemDecoration(OffsetItemDecoration(binding.toolbar.getToolbarLayoutHeight()))
        binding.rvContainer.adapter = ContentAdapter(requireContext(), arrayListOf()).apply {
            mAdapter = this
            initStatusView()
            pageLoading()
            setAutoLoadMoreListener {
                viewModel.requestHomeData(LoadStatus.LOAD_MORE)
            }
            setLoadErrorListener(object : LoadErrorListener {
                override fun pageReload() {
                    viewModel.requestHomeData(LoadStatus.REFRESH)
                }

                override fun itemReload() {
                    viewModel.requestHomeData(LoadStatus.RELOAD)
                }
            })
            setOnItemClickListener { _, entity, _ ->
                entity?.content?.apply {
                    findNavController().navigateDetails(title, link, id, collect)
                }
            }
        }.proxy

        binding.refreshView.setOnRefreshListener {
            mAdapter.openLoadMore()
            viewModel.requestHomeData(LoadStatus.REFRESH)
        }
    }

    override fun bindData() {
        LiveDataBus.getInstance().injectBus(this)

        viewModel.contentListLive.observeState<PageUiData<HomeContent>>(this, success = {
            binding.refreshView.isRefreshing = false
            mAdapter.submitData(it, diff = true)
        }, error = {
            binding.refreshView.isRefreshing = false
            mAdapter.submitFailed()
            actionFailed(it)
        })
    }

    @InjectBus(NavigationSelectEvent.key, isActive = true)
    fun navigationEvent(event: NavigationSelectEvent) {
        if (event.title == getString(R.string.nav_home) && isResumed) {
            binding.rvContainer.smoothScrollToTop()
        }
    }

}