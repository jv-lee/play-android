package com.lee.playandroid.home.view

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.adapter.extensions.bindAllListener
import com.lee.library.adapter.extensions.unbindAllListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.delayBackEvent
import com.lee.library.extensions.launchAndRepeatWithViewLifecycle
import com.lee.library.extensions.smoothScrollToTop
import com.lee.library.livedatabus.InjectBus
import com.lee.library.livedatabus.LiveDataBus
import com.lee.library.viewstate.LoadStatus
import com.lee.library.viewstate.collectState
import com.lee.library.widget.banner.BannerView
import com.lee.playandroid.home.R
import com.lee.playandroid.home.bean.HomeContent
import com.lee.playandroid.home.databinding.FragmentHomeBinding
import com.lee.playandroid.home.view.adapter.ContentAdapter
import com.lee.playandroid.home.viewmodel.HomeViewAction
import com.lee.playandroid.home.viewmodel.HomeViewModel
import com.lee.playandroid.library.common.entity.NavigationSelectEvent
import com.lee.playandroid.library.common.entity.PageUiData
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.library.common.ui.extensions.setThemeGradientBackground
import com.lee.playandroid.library.common.ui.widget.MainLoadResource
import com.lee.playandroid.library.common.ui.widget.OffsetItemDecoration
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.router.navigateSearch

/**
 * @author jv.lee
 * @date 2021/11/2
 * @description 首页第一个Tab 主页
 */
class HomeFragment : BaseNavigationFragment(R.layout.fragment_home),
    View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
    BaseViewAdapter.OnItemClickListener<HomeContent>,
    BaseViewAdapter.AutoLoadMoreListener,
    BaseViewAdapter.LoadErrorListener {

    private val viewModel by viewModels<HomeViewModel>()

    private val binding by binding(FragmentHomeBinding::bind)

    private var mAdapter: ContentAdapter? = null

    override fun bindView() {
        // 拦截back处理
        delayBackEvent()
        // 设置toolbar主题渐变色背景
        binding.toolbar.setThemeGradientBackground()

        binding.rvContainer.addSaveStateViewType(BannerView::class.java)
        binding.rvContainer.addItemDecoration(OffsetItemDecoration(binding.toolbar.getToolbarLayoutHeight()))
        if (binding.rvContainer.adapter == null) {
            binding.rvContainer.adapter = ContentAdapter(requireContext(), arrayListOf()).apply {
                mAdapter = this
                setLoadResource(MainLoadResource())
                initStatusView()
                pageLoading()

            }.proxy
        }
    }

    override fun bindData() {
        LiveDataBus.getInstance().injectBus(this)

        launchAndRepeatWithViewLifecycle {
            viewModel.contentListFlow.collectState<PageUiData<HomeContent>>(success = {
                binding.refreshView.isRefreshing = false
                mAdapter?.submitData(it, diff = true)
            }, error = {
                binding.refreshView.isRefreshing = false
                mAdapter?.submitFailed()
                actionFailed(it)
            })
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.ivSearch -> findNavController().navigateSearch()
        }
    }

    override fun onItemClick(view: View?, entity: HomeContent?, position: Int) {
        entity?.content?.apply {
            findNavController().navigateDetails(title, link, id, collect)
        }
    }

    override fun onRefresh() {
        mAdapter?.openLoadMore()
        viewModel.dispatch(HomeViewAction.RequestPage(LoadStatus.REFRESH))
    }

    override fun autoLoadMore() {
        viewModel.dispatch(HomeViewAction.RequestPage(LoadStatus.LOAD_MORE))
    }

    override fun pageReload() {
        viewModel.dispatch(HomeViewAction.RequestPage(LoadStatus.REFRESH))
    }

    override fun itemReload() {
        viewModel.dispatch(HomeViewAction.RequestPage(LoadStatus.RELOAD))
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        mAdapter?.bindAllListener(this)
        binding.ivSearch.setOnClickListener(this)
        binding.refreshView.setOnRefreshListener(this)
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        mAdapter?.unbindAllListener()
        binding.ivSearch.setOnClickListener(null)
        binding.refreshView.setOnRefreshListener(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvContainer.adapter = null
        mAdapter = null
    }

    @InjectBus(NavigationSelectEvent.key, isActive = true)
    fun navigationEvent(event: NavigationSelectEvent) {
        if (event.title == getString(R.string.nav_home) && isResumed) {
            binding.rvContainer.smoothScrollToTop()
        }
    }

}