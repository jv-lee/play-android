package com.lee.playandroid.system.ui

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lee.library.adapter.core.VerticalTabAdapter
import com.lee.library.adapter.page.submitSinglePage
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.findParentFragment
import com.lee.library.extensions.setMargin
import com.lee.library.extensions.smoothScrollToTop
import com.lee.library.livedatabus.InjectBus
import com.lee.library.livedatabus.LiveDataBus
import com.lee.library.mvvm.ui.observeState
import com.lee.library.widget.StatusLayout
import com.lee.library.widget.layoutmanager.LinearTopSmoothScroller
import com.lee.playandroid.library.common.entity.NavigationItem
import com.lee.playandroid.library.common.entity.NavigationSelectEvent
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.system.R
import com.lee.playandroid.system.databinding.FragmentNavigationBinding
import com.lee.playandroid.system.ui.adapter.NavigationContentAdapter
import com.lee.playandroid.system.ui.adapter.NavigationTabAdapter
import com.lee.playandroid.system.viewmodel.NavigationViewModel

/**
 * @author jv.lee
 * @date 2021/11/10
 * @description  导航Fragment
 * @see SystemFragment 体系Fragment下第二个Tab
 */
class NavigationFragment : BaseFragment(R.layout.fragment_navigation),
    StatusLayout.OnReloadListener {

    private val viewModel by viewModels<NavigationViewModel>()

    private val binding by binding(FragmentNavigationBinding::bind)

    private lateinit var mNavigationTabAdapter: NavigationTabAdapter
    private lateinit var mNavigationContentAdapter: NavigationContentAdapter

    override fun bindView() {
        findParentFragment<SystemFragment>()?.parentBindingAction {
            binding.rvTab.setMargin(
                top = it.toolbar.getToolbarLayoutHeight(),
                bottom = resources.getDimension(R.dimen.navigation_bar_height).toInt()
            )
            binding.rvContainer.setMargin(
                top = it.toolbar.getToolbarLayoutHeight(),
                bottom = resources.getDimension(R.dimen.navigation_bar_height).toInt()
            )
        }

        mNavigationTabAdapter = NavigationTabAdapter(arrayListOf())
        binding.rvTab.adapter = mNavigationTabAdapter
        binding.rvTab.itemAnimator = null

        mNavigationContentAdapter = NavigationContentAdapter(requireContext(), arrayListOf())
        binding.rvContainer.adapter = mNavigationContentAdapter.proxy

        binding.statusLayout.setStatus(StatusLayout.STATUS_LOADING)
        binding.statusLayout.setOnReloadListener(this)

        navigationTabSelectListener()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun bindData() {
        LiveDataBus.getInstance().injectBus(this)

        viewModel.navigationLive.observeState<List<NavigationItem>>(this, success = {
            binding.statusLayout.setStatus(StatusLayout.STATUS_DATA)
            mNavigationTabAdapter.updateNotify(it)
            mNavigationContentAdapter.submitSinglePage(it)
        }, error = {
            if (mNavigationTabAdapter.data.isNullOrEmpty()) {
                binding.statusLayout.setStatus(StatusLayout.STATUS_DATA_ERROR)
            }
            actionFailed(it)
        })

        viewModel.selectTabLive.observe(this, {
            mNavigationTabAdapter.selectItem(it)
        })
    }

    override fun lazyLoad() {
        super.lazyLoad()
        viewModel.requestNavigationData()
    }

    override fun onReload() {
        viewModel.requestNavigationData()
    }

    /**
     * 双向列表互动监听器处理
     */
    private fun navigationTabSelectListener() {
        var isLock = false

        mNavigationTabAdapter.setItemClickCall(object : VerticalTabAdapter.ItemClickCall {
            override fun itemClick(position: Int) {
                val selectItem = mNavigationTabAdapter.data[position]
                val selectPosition = mNavigationContentAdapter.data.indexOf(selectItem)
                val scroller = LinearTopSmoothScroller(requireContext(), selectPosition)

                binding.rvContainer.layoutManager?.startSmoothScroll(scroller)
                viewModel.selectTabIndex(selectPosition)
                isLock = true
            }
        })
        binding.rvContainer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!isLock) {
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val position = layoutManager.findFirstVisibleItemPosition()
                        val scroller = LinearTopSmoothScroller(requireContext(), position)

                        binding.rvTab.layoutManager?.startSmoothScroll(scroller)
                        viewModel.selectTabIndex(position)
                    }
                    isLock = false
                }
            }
        })
    }

    @InjectBus(NavigationSelectEvent.key, isActive = true)
    fun navigationEvent(event: NavigationSelectEvent) {
        if (event.title == getString(R.string.nav_system) && isResumed) {
            viewModel.selectTabIndex(0)
            binding.rvTab.smoothScrollToTop()
            binding.rvContainer.smoothScrollToTop()
        }
    }

}