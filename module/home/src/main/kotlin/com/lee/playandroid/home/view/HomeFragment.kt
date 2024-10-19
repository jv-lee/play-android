package com.lee.playandroid.home.view

import android.content.res.Configuration
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lee.playandroid.base.adapter.base.BaseViewAdapter
import com.lee.playandroid.base.adapter.extensions.bindAllListener
import com.lee.playandroid.base.adapter.extensions.unbindAllListener
import com.lee.playandroid.base.adapter.page.submitData
import com.lee.playandroid.base.adapter.page.submitFailed
import com.lee.playandroid.base.base.BaseBindingNavigationFragment
import com.lee.playandroid.base.extensions.delayBackEvent
import com.lee.playandroid.base.extensions.smoothScrollToTop
import com.lee.playandroid.base.livedatabus.InjectBus
import com.lee.playandroid.base.livedatabus.LiveDataBus
import com.lee.playandroid.base.uistate.LoadStatus
import com.lee.playandroid.base.uistate.collectCallback
import com.lee.playandroid.base.widget.banner.BannerView
import com.lee.playandroid.common.entity.NavigationSelectEvent
import com.lee.playandroid.common.entity.PageUiData
import com.lee.playandroid.common.extensions.actionFailed
import com.lee.playandroid.common.ui.extensions.setThemeGradientBackground
import com.lee.playandroid.common.ui.widget.MainLoadResource
import com.lee.playandroid.common.ui.widget.OffsetItemDecoration
import com.lee.playandroid.common.R as CR
import com.lee.playandroid.home.R
import com.lee.playandroid.home.bean.HomeContent
import com.lee.playandroid.home.databinding.FragmentHomeBinding
import com.lee.playandroid.home.view.adapter.ContentAdapter
import com.lee.playandroid.home.viewmodel.HomeViewIntent
import com.lee.playandroid.home.viewmodel.HomeViewModel
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.router.navigateSearch

/**
 * 首页第一个Tab 主页
 * @author jv.lee
 * @date 2021/11/2
 */
class HomeFragment :
    BaseBindingNavigationFragment<FragmentHomeBinding>(),
    View.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener,
    BaseViewAdapter.OnItemClickListener<HomeContent>,
    BaseViewAdapter.AutoLoadMoreListener,
    BaseViewAdapter.LoadErrorListener {

    private val viewModel by viewModels<HomeViewModel>()

    private var mAdapter: ContentAdapter? = null

    override fun bindView() {
        // 拦截back处理
        delayBackEvent()
        // 设置toolbar主题渐变色背景
        mBinding.toolbar.setThemeGradientBackground()
        mBinding.refreshView.setProgressViewOffset(
            false,
            mBinding.toolbar.getToolbarLayoutHeight() / 2,
            mBinding.refreshView.progressViewEndOffset + mBinding.toolbar.getToolbarLayoutHeight() / 2
        )

        mBinding.rvContainer.addSaveStateViewType(BannerView::class.java)
        mBinding.rvContainer.addItemDecoration(
            OffsetItemDecoration(mBinding.toolbar.getToolbarLayoutHeight())
        )
        if (mBinding.rvContainer.adapter == null) {
            ContentAdapter(requireContext()).apply {
                mAdapter = this
                bindRecyclerView(mBinding.rvContainer, MainLoadResource())
            }
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        LiveDataBus.instance.injectBus(this@HomeFragment)

        launchOnLifecycle {
            viewModel.contentListFlow.collectCallback<PageUiData<HomeContent>>(
                success = {
                    mBinding.refreshView.isRefreshing = false
                    mAdapter?.submitData(it, diff = true)
                },
                error = {
                    mBinding.refreshView.isRefreshing = false
                    mAdapter?.submitFailed()
                    actionFailed(it)
                }
            )
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 屏幕发生变化后重新计算categoryItem宽度
        mAdapter?.takeIf { it.itemCount > 2 }?.run { notifyItemChanged(1) }
    }

    override fun onClick(v: View?) {
        when (v) {
            mBinding.ivSearch -> findNavController().navigateSearch()
        }
    }

    override fun onItemClick(view: View, entity: HomeContent, position: Int) {
        entity.content?.run { findNavController().navigateDetails(title, link, id, collect) }
    }

    override fun onRefresh() {
        viewModel.dispatch(HomeViewIntent.RequestPage(LoadStatus.REFRESH))
    }

    override fun autoLoadMore() {
        viewModel.dispatch(HomeViewIntent.RequestPage(LoadStatus.LOAD_MORE))
    }

    override fun pageReload() {
        viewModel.dispatch(HomeViewIntent.RequestPage(LoadStatus.REFRESH))
    }

    override fun itemReload() {
        viewModel.dispatch(HomeViewIntent.RequestPage(LoadStatus.RELOAD))
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        mAdapter?.bindAllListener(this)
        mBinding.ivSearch.setOnClickListener(this)
        mBinding.refreshView.setOnRefreshListener(this)
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        mAdapter?.unbindAllListener()
        mBinding.ivSearch.setOnClickListener(null)
        mBinding.refreshView.setOnRefreshListener(null)
    }

    override fun onDestroyView() {
        mBinding.rvContainer.adapter = null
        mAdapter = null
        super.onDestroyView()
    }

    @InjectBus
    fun navigationEvent(event: NavigationSelectEvent) {
        if (event.title == getString(CR.string.nav_home) && isResumed) {
            mBinding.rvContainer.smoothScrollToTop()
        }
    }
}