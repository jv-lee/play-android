package com.lee.playandroid.square.ui.fragment

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
import com.lee.playandroid.base.extensions.toast
import com.lee.playandroid.base.livedatabus.InjectBus
import com.lee.playandroid.base.livedatabus.LiveDataBus
import com.lee.playandroid.base.uistate.LoadStatus
import com.lee.playandroid.base.uistate.collectCallback
import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.entity.NavigationSelectEvent
import com.lee.playandroid.common.entity.PageData
import com.lee.playandroid.common.extensions.actionFailed
import com.lee.playandroid.common.ui.extensions.setThemeGradientBackground
import com.lee.playandroid.common.ui.widget.MainLoadResource
import com.lee.playandroid.common.ui.widget.OffsetItemDecoration
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.router.navigateLogin
import com.lee.playandroid.square.R
import com.lee.playandroid.square.databinding.FragmentSquareBinding
import com.lee.playandroid.square.ui.adapter.SquareAdapter
import com.lee.playandroid.square.viewmodel.SquareViewIntent
import com.lee.playandroid.square.viewmodel.SquareViewModel
import com.lee.playandroid.common.R as CR

/**
 * 首页第二个Tab 广场页面
 * @author jv.lee
 * @date 2021/12/13
 */
class SquareFragment :
    BaseBindingNavigationFragment<FragmentSquareBinding>(),
    View.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener,
    BaseViewAdapter.OnItemClickListener<Content>,
    BaseViewAdapter.AutoLoadMoreListener,
    BaseViewAdapter.LoadErrorListener {

    private val viewModel by viewModels<SquareViewModel>()

    private var mAdapter: SquareAdapter? = null

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

        mBinding.rvContainer.addItemDecoration(
            OffsetItemDecoration(mBinding.toolbar.getToolbarLayoutHeight())
        )
        if (mBinding.rvContainer.adapter == null) {
            SquareAdapter(requireContext()).apply {
                mAdapter = this
                bindRecyclerView(mBinding.rvContainer, MainLoadResource())
            }
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        LiveDataBus.instance.injectBus(this@SquareFragment)

        launchOnLifecycle {
            viewModel.squareFlow.collectCallback<PageData<Content>>(
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

    override fun onClick(v: View?) {
        // 需要校验登陆状态
        if (viewModel.accountService.isLogin()) {
            findNavController().navigate(R.id.action_square_fragment_to_create_share_fragment)
        } else {
            toast(getString(CR.string.login_message))
            findNavController().navigateLogin()
        }
    }

    override fun onItemClick(view: View, entity: Content, position: Int) {
        entity.run { findNavController().navigateDetails(title, link, id, collect) }
    }

    override fun onRefresh() {
        viewModel.dispatch(SquareViewIntent.RequestPage(LoadStatus.REFRESH))
    }

    override fun autoLoadMore() {
        viewModel.dispatch(SquareViewIntent.RequestPage(LoadStatus.LOAD_MORE))
    }

    override fun pageReload() {
        viewModel.dispatch(SquareViewIntent.RequestPage(LoadStatus.REFRESH))
    }

    override fun itemReload() {
        viewModel.dispatch(SquareViewIntent.RequestPage(LoadStatus.RELOAD))
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        mAdapter?.bindAllListener(this)
        mBinding.ivCreate.setOnClickListener(this)
        mBinding.refreshView.setOnRefreshListener(this)
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        mAdapter?.unbindAllListener()
        mBinding.ivCreate.setOnClickListener(null)
        mBinding.refreshView.setOnRefreshListener(null)
    }

    override fun onDestroyView() {
        mBinding.rvContainer.adapter = null
        mAdapter = null
        super.onDestroyView()
    }

    @InjectBus
    fun navigationEvent(event: NavigationSelectEvent) {
        if (event.title == getString(CR.string.nav_square) && isResumed) {
            mBinding.rvContainer.smoothScrollToTop()
        }
    }
}