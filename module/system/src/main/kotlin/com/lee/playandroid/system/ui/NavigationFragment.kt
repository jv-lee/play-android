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
import com.lee.library.extensions.smoothScrollToTop
import com.lee.library.extensions.toast
import com.lee.library.livedatabus.InjectBus
import com.lee.library.livedatabus.LiveDataBus
import com.lee.library.mvvm.ui.observeState
import com.lee.playandroid.library.common.entity.NavigationItem
import com.lee.playandroid.library.common.entity.NavigationSelectEvent
import com.lee.playandroid.library.common.ui.widget.OffsetItemDecoration
import com.lee.playandroid.system.R
import com.lee.playandroid.system.databinding.FragmentNavigationBinding
import com.lee.playandroid.system.ui.adapter.NavigationContentAdapter
import com.lee.playandroid.system.ui.adapter.NavigationTabAdapter
import com.lee.playandroid.system.viewmodel.NavigationViewModel

/**
 * @author jv.lee
 * @data 2021/11/10
 * @description  导航Fragment
 * @see SystemFragment 体系Fragment下第二个Tab
 */
class NavigationFragment : BaseFragment(R.layout.fragment_navigation) {

    private val viewModel by viewModels<NavigationViewModel>()

    private val binding by binding(FragmentNavigationBinding::bind)

    private lateinit var mNavigationTabAdapter: NavigationTabAdapter
    private lateinit var mNavigationContentAdapter: NavigationContentAdapter

    override fun bindView() {
        findParentFragment<SystemFragment>()?.parentBindingAction {
            val decoration =
                OffsetItemDecoration(
                    offsetTop = it.toolbar.getToolbarLayoutHeight(),
                    offsetBottom = resources.getDimension(R.dimen.navigation_bar_height).toInt()
                )
            binding.rvTab.addItemDecoration(decoration)
            binding.rvContainer.addItemDecoration(decoration)
        }

        mNavigationTabAdapter = NavigationTabAdapter(arrayListOf())
        binding.rvTab.adapter = mNavigationTabAdapter


        mNavigationContentAdapter = NavigationContentAdapter(requireContext(), arrayListOf())
        binding.rvContainer.adapter = mNavigationContentAdapter.proxy

        mNavigationTabAdapter.setItemClickCall(object : VerticalTabAdapter.ItemClickCall {
            override fun itemClick(position: Int) {
                val selectItem = mNavigationTabAdapter.data[position]
                val selectPosition = mNavigationContentAdapter.data.indexOf(selectItem)
                binding.rvContainer.smoothScrollToPosition(selectPosition)
            }
        })
        binding.rvContainer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                    mNavigationTabAdapter.selectItem(position)
                    binding.rvTab.smoothScrollToPosition(position)
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun bindData() {
        viewModel.navigationLive.observeState<List<NavigationItem>>(this, success = {
            mNavigationTabAdapter.updateNotify(it)
            mNavigationContentAdapter.submitSinglePage(it)
        }, error = {
            toast(it.message)
        }, loading = {
        })
    }

    override fun bindEvent() {
        LiveDataBus.getInstance().injectBus(this)
    }

    override fun lazyLoad() {
        viewModel.requestNavigationData()
    }

    @InjectBus(NavigationSelectEvent.key, isActive = true)
    fun navigationEvent(event: NavigationSelectEvent) {
        if (event.title == getString(R.string.nav_system) && isResumed) {
            mNavigationTabAdapter.selectItem(0)
            binding.rvTab.smoothScrollToTop()
            binding.rvContainer.smoothScrollToTop()
        }
    }

}