package com.lee.playandroid.square.ui.fragment

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
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
import com.lee.library.extensions.smoothScrollToTop
import com.lee.library.extensions.toast
import com.lee.library.livedatabus.InjectBus
import com.lee.library.livedatabus.LiveDataBus
import com.lee.library.viewstate.LoadStatus
import com.lee.library.viewstate.collectState
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
import com.lee.playandroid.square.viewmodel.SquareViewAction
import com.lee.playandroid.square.viewmodel.SquareViewModel
import com.lee.playandroid.common.R as CR

/**
 * 首页第二个Tab 广场页面
 * @author jv.lee
 * @date 2021/12/13
 */
class SquareFragment : BaseNavigationFragment(R.layout.fragment_square),
    View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
    BaseViewAdapter.OnItemClickListener<Content>,
    BaseViewAdapter.AutoLoadMoreListener,
    BaseViewAdapter.LoadErrorListener {

    private val viewModel by viewModels<SquareViewModel>()

    private val binding by binding(FragmentSquareBinding::bind)

    private var mAdapter: SquareAdapter? = null

    override fun bindView() {
        // 拦截back处理
        delayBackEvent()
        // 设置toolbar主题渐变色背景
        binding.toolbar.setThemeGradientBackground()

        binding.rvContainer.addItemDecoration(OffsetItemDecoration(binding.toolbar.getToolbarLayoutHeight()))
        if (binding.rvContainer.adapter == null) {
            binding.rvContainer.adapter = SquareAdapter(requireContext(), arrayListOf()).apply {
                mAdapter = this
                setLoadResource(MainLoadResource())
                initStatusView()
                pageLoading()
            }.proxy
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        LiveDataBus.instance.injectBus(this@SquareFragment)

        launchWhenResumed {
            viewModel.squareFlow.collectState<PageData<Content>>(success = {
                binding.refreshView.isRefreshing = false
                mAdapter?.submitData(it)
            }, error = {
                binding.refreshView.isRefreshing = false
                mAdapter?.submitFailed()
                actionFailed(it)
            })
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

    override fun onItemClick(view: View?, entity: Content?, position: Int) {
        entity?.run { findNavController().navigateDetails(title, link, id, collect) }
    }

    override fun onRefresh() {
        viewModel.dispatch(SquareViewAction.RequestPage(LoadStatus.REFRESH))
    }

    override fun autoLoadMore() {
        viewModel.dispatch(SquareViewAction.RequestPage(LoadStatus.LOAD_MORE))
    }

    override fun pageReload() {
        viewModel.dispatch(SquareViewAction.RequestPage(LoadStatus.REFRESH))
    }

    override fun itemReload() {
        viewModel.dispatch(SquareViewAction.RequestPage(LoadStatus.RELOAD))
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        mAdapter?.bindAllListener(this)
        binding.ivCreate.setOnClickListener(this)
        binding.refreshView.setOnRefreshListener(this)
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        mAdapter?.unbindAllListener()
        binding.ivCreate.setOnClickListener(null)
        binding.refreshView.setOnRefreshListener(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvContainer.adapter = null
        mAdapter = null
    }

    @InjectBus
    fun navigationEvent(event: NavigationSelectEvent) {
        if (event.title == getString(R.string.nav_square) && isResumed) {
            binding.rvContainer.smoothScrollToTop()
        }
    }

}