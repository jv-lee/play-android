package com.lee.playandroid.system.ui

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import com.lee.library.adapter.page.submitSinglePage
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.findParentFragment
import com.lee.library.extensions.setMargin
import com.lee.library.extensions.smoothScrollToTop
import com.lee.library.livedatabus.InjectBus
import com.lee.library.livedatabus.LiveDataBus
import com.lee.library.viewstate.LoadStatus
import com.lee.library.viewstate.stateObserve
import com.lee.library.widget.StatusLayout
import com.lee.playandroid.library.common.entity.NavigationItem
import com.lee.playandroid.library.common.entity.NavigationSelectEvent
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.library.common.ui.extensions.bindTabLinkage
import com.lee.playandroid.system.R
import com.lee.playandroid.system.databinding.FragmentNavigationContentBinding
import com.lee.playandroid.system.ui.adapter.NavigationContentAdapter
import com.lee.playandroid.system.ui.adapter.NavigationContentTabAdapter
import com.lee.playandroid.system.viewmodel.NavigationContentViewModel

/**
 * @author jv.lee
 * @date 2021/11/10
 * @description  导航Fragment
 * @see SystemFragment 体系Fragment下第二个Tab
 */
class NavigationContentFragment : BaseNavigationFragment(R.layout.fragment_navigation_content),
    StatusLayout.OnReloadListener {

    private val viewModel by viewModels<NavigationContentViewModel>()

    private val binding by binding(FragmentNavigationContentBinding::bind)

    private var mNavigationTabAdapter: NavigationContentTabAdapter? = null
    private var mNavigationContentAdapter: NavigationContentAdapter? = null

    override fun bindView() {
        binding.statusLayout.setStatus(StatusLayout.STATUS_LOADING)
        binding.statusLayout.setOnReloadListener(this)

        findParentFragment<SystemFragment>()?.parentBindingAction {
            val topOffset = (it.toolbar.getToolbarLayoutHeight() * 0.9).toInt()
            val bottomOffset = resources.getDimension(R.dimen.navigation_bar_height).toInt()

            binding.rvTab.setMargin(top = topOffset, bottom = bottomOffset)
            binding.rvContainer.setMargin(top = topOffset, bottom = bottomOffset)
        }

        if (binding.rvTab.adapter == null) {
            binding.rvTab.itemAnimator = null
            binding.rvTab.adapter = NavigationContentTabAdapter(arrayListOf()).apply {
                mNavigationTabAdapter = this
            }
        }

        if (binding.rvContainer.adapter == null) {
            binding.rvContainer.adapter =
                NavigationContentAdapter(requireContext(), arrayListOf()).apply {
                    mNavigationContentAdapter = this
                }.proxy
        }

        mNavigationTabAdapter?.bindTabLinkage(binding.rvTab, binding.rvContainer) { position ->
            viewModel.selectTabIndex(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun bindData() {
        LiveDataBus.getInstance().injectBus(this)

        viewModel.navigationLive.stateObserve<List<NavigationItem>>(viewLifecycleOwner, success = {
            binding.statusLayout.setStatus(StatusLayout.STATUS_DATA)
            mNavigationTabAdapter?.updateNotify(it)
            mNavigationContentAdapter?.submitSinglePage(it)
        }, error = {
            if (mNavigationTabAdapter?.data.isNullOrEmpty()) {
                binding.statusLayout.setStatus(StatusLayout.STATUS_DATA_ERROR)
            }
            actionFailed(it)
        })

        viewModel.selectTabLive.observe(viewLifecycleOwner) {
            mNavigationTabAdapter?.selectItem(it)
        }
    }

    override fun lazyLoad() {
        super.lazyLoad()
        viewModel.requestNavigationData(_root_ide_package_.com.lee.library.viewstate.LoadStatus.INIT)
    }

    override fun onReload() {
        viewModel.requestNavigationData(_root_ide_package_.com.lee.library.viewstate.LoadStatus.RELOAD)
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