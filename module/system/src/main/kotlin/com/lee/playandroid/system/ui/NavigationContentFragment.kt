package com.lee.playandroid.system.ui

import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import com.lee.playandroid.base.adapter.page.submitSinglePage
import com.lee.playandroid.base.base.BaseBindingNavigationFragment
import com.lee.playandroid.base.extensions.collectState
import com.lee.playandroid.base.extensions.findParentFragment
import com.lee.playandroid.base.extensions.setMargin
import com.lee.playandroid.base.extensions.smoothScrollToTop
import com.lee.playandroid.base.livedatabus.InjectBus
import com.lee.playandroid.base.livedatabus.LiveDataBus
import com.lee.playandroid.base.widget.StatusLayout
import com.lee.playandroid.common.entity.NavigationSelectEvent
import com.lee.playandroid.common.extensions.actionFailed
import com.lee.playandroid.common.ui.extensions.bindTabLinkage
import com.lee.playandroid.common.ui.widget.MainLoadResource
import com.lee.playandroid.system.R
import com.lee.playandroid.system.databinding.FragmentNavigationContentBinding
import com.lee.playandroid.system.ui.adapter.NavigationContentAdapter
import com.lee.playandroid.system.ui.adapter.NavigationContentTabAdapter
import com.lee.playandroid.system.viewmodel.NavigationContentViewEvent
import com.lee.playandroid.system.viewmodel.NavigationContentViewIntent
import com.lee.playandroid.system.viewmodel.NavigationContentViewModel
import com.lee.playandroid.system.viewmodel.NavigationContentViewState

/**
 * 导航Fragment
 * @see SystemFragment 体系Fragment下第二个Tab
 * @author jv.lee
 * @date 2021/11/10
 */
class NavigationContentFragment :
    BaseBindingNavigationFragment<FragmentNavigationContentBinding>(),
    StatusLayout.OnReloadListener {

    private val viewModel by viewModels<NavigationContentViewModel>()

    private var mNavigationTabAdapter: NavigationContentTabAdapter? = null
    private var mNavigationContentAdapter: NavigationContentAdapter? = null

    override fun bindView() {
        mBinding.statusLayout.setOnReloadListener(this)

        findParentFragment<SystemFragment>()?.parentBindingAction {
            val topOffset = (toolbar.getToolbarLayoutHeight() * 0.9).toInt()
            val bottomOffset = resources.getDimension(R.dimen.navigation_bar_height).toInt()

            mBinding.rvTab.setMargin(top = topOffset, bottom = bottomOffset)
            mBinding.rvContainer.setMargin(top = topOffset, bottom = bottomOffset)
        }

        if (mBinding.rvTab.adapter == null) {
            mBinding.rvTab.itemAnimator = null
            mBinding.rvTab.adapter = NavigationContentTabAdapter(arrayListOf()).apply {
                mNavigationTabAdapter = this
            }
        }

        if (mBinding.rvContainer.adapter == null) {
            mBinding.rvContainer.adapter =
                NavigationContentAdapter(requireContext()).apply {
                    mNavigationContentAdapter = this
                    setLoadResource(MainLoadResource())
                }.getProxy()
        }

        mNavigationTabAdapter?.bindTabLinkage(mBinding.rvTab, mBinding.rvContainer) { position ->
            viewModel.dispatch(NavigationContentViewIntent.SelectTabIndex(position))
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        LiveDataBus.instance.injectBus(this@NavigationContentFragment)

        launchWhenResumed {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is NavigationContentViewEvent.RequestFailed -> {
                        actionFailed(event.error)
                        if (mNavigationTabAdapter?.data.isNullOrEmpty()) {
                            mBinding.statusLayout.setStatus(StatusLayout.STATUS_DATA_ERROR)
                        }
                    }
                }
            }
        }

        viewModel.viewStates.run {
            launchWhenStarted {
                collectState(
                    NavigationContentViewState::navigationItemList,
                    NavigationContentViewState::isLoading
                ) { data, isLoading ->
                    if (isLoading) mBinding.statusLayout.setStatus(StatusLayout.STATUS_LOADING)
                    else mBinding.statusLayout.setStatus(StatusLayout.STATUS_DATA)
                    mNavigationTabAdapter?.updateNotify(data)
                    mNavigationContentAdapter?.submitSinglePage(data)
                }
            }
            launchWhenStarted {
                collectState(NavigationContentViewState::selectedTabIndex) {
                    mNavigationTabAdapter?.selectItem(it)
                }
            }
        }
    }

    override fun lazyLoad() {
        super.lazyLoad()
        viewModel.dispatch(NavigationContentViewIntent.RequestData)
    }

    override fun onReload() {
        viewModel.dispatch(NavigationContentViewIntent.RequestData)
    }

    @InjectBus
    fun navigationEvent(event: NavigationSelectEvent) {
        if (event.title == getString(R.string.nav_system) && isResumed) {
            viewModel.dispatch(NavigationContentViewIntent.SelectTabIndex(0))
            mBinding.rvTab.smoothScrollToTop()
            mBinding.rvContainer.smoothScrollToTop()
        }
    }
}