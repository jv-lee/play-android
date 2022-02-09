package com.lee.playandroid.square.ui.fragment

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.adapter.extensions.bindAllListener
import com.lee.library.adapter.page.submitData
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.binding
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
class SquareFragment : BaseNavigationFragment(R.layout.fragment_square),
    View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
    BaseViewAdapter.OnItemClickListener<Content>,
    BaseViewAdapter.AutoLoadMoreListener,
    BaseViewAdapter.LoadErrorListener {

    private val viewModel by viewModels<SquareViewModel>()

    private val binding by binding(FragmentSquareBinding::bind)

    private var mAdapter: SquareAdapter? = null

    override fun bindView() {
        binding.toolbar.setThemeGradientBackground()

        binding.rvContainer.addItemDecoration(OffsetItemDecoration(binding.toolbar.getToolbarLayoutHeight()))
        if (binding.rvContainer.adapter == null) {
            binding.rvContainer.adapter = SquareAdapter(requireContext(), arrayListOf()).apply {
                mAdapter = this
                initStatusView()
                pageLoading()
                bindAllListener(this@SquareFragment)
            }.proxy
        }

        binding.ivCreate.setOnClickListener(this)
        binding.refreshView.setOnRefreshListener(this)
    }

    override fun bindData() {
        LiveDataBus.getInstance().injectBus(this)

        viewModel.squareLive.observeState<PageData<Content>>(viewLifecycleOwner, success = {
            binding.refreshView.isRefreshing = false
            mAdapter?.submitData(it)
        }, error = {
            binding.refreshView.isRefreshing = false
            mAdapter?.loadFailed()
            actionFailed(it)
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.ivCreate -> findNavController()
                .navigate(R.id.action_square_fragment_to_create_share_fragment)
        }
    }

    override fun onRefresh() {
        mAdapter?.openLoadMore()
        viewModel.requestSquareData(LoadStatus.REFRESH)
    }

    override fun onItemClick(view: View?, entity: Content?, position: Int) {
        entity?.apply {
            findNavController().navigateDetails(title, link, id, collect)
        }
    }

    override fun autoLoadMore() {
        viewModel.requestSquareData(LoadStatus.LOAD_MORE)
    }

    override fun pageReload() {
        viewModel.requestSquareData(LoadStatus.REFRESH)
    }

    override fun itemReload() {
        viewModel.requestSquareData(LoadStatus.RELOAD)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvContainer.adapter = null
        mAdapter = null
    }

    @InjectBus(NavigationSelectEvent.key, isActive = true)
    fun navigationEvent(event: NavigationSelectEvent) {
        if (event.title == getString(R.string.nav_square) && isResumed) {
            binding.rvContainer.smoothScrollToTop()
        }
    }

}