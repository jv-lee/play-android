package com.lee.playandroid.system.ui

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.base.adapter.base.BaseViewAdapter
import com.lee.playandroid.base.adapter.extensions.bindAllListener
import com.lee.playandroid.base.adapter.page.submitFailed
import com.lee.playandroid.base.adapter.page.submitSinglePage
import com.lee.playandroid.base.base.BaseNavigationFragment
import com.lee.playandroid.base.extensions.binding
import com.lee.playandroid.base.extensions.findParentFragment
import com.lee.playandroid.base.extensions.smoothScrollToTop
import com.lee.playandroid.base.livedatabus.InjectBus
import com.lee.playandroid.base.livedatabus.LiveDataBus
import com.lee.playandroid.base.viewstate.collectState
import com.lee.playandroid.common.entity.NavigationSelectEvent
import com.lee.playandroid.common.entity.ParentTab
import com.lee.playandroid.common.extensions.actionFailed
import com.lee.playandroid.common.ui.widget.MainLoadResource
import com.lee.playandroid.common.ui.widget.OffsetItemDecoration
import com.lee.playandroid.system.R
import com.lee.playandroid.system.databinding.FragmentSystemContentBinding
import com.lee.playandroid.system.ui.adapter.SystemContentAdapter
import com.lee.playandroid.system.viewmodel.SystemContentViewAction
import com.lee.playandroid.system.viewmodel.SystemContentViewEvent
import com.lee.playandroid.system.viewmodel.SystemContentViewModel
import com.lee.playandroid.system.viewmodel.SystemContentViewState
import kotlinx.coroutines.flow.collect

/**
 * 体系列表Fragment
 * @see SystemFragment 体系Fragment下第一个Tab
 * @author jv.lee
 * @date 2021/11/10
 */
class SystemContentFragment :
    BaseNavigationFragment(R.layout.fragment_system_content),
    BaseViewAdapter.OnItemClickListener<ParentTab>,
    BaseViewAdapter.LoadErrorListener {

    private val viewModel by viewModels<SystemContentViewModel>()

    private val binding by binding(FragmentSystemContentBinding::bind)

    private var mAdapter: SystemContentAdapter? = null

    override fun bindView() {
        // 根据父Fragment toolbar高度设置ItemDecoration来控制显示间隔
        findParentFragment<SystemFragment>()?.parentBindingAction {
            binding.rvContainer.addItemDecoration(
                OffsetItemDecoration(toolbar.getToolbarLayoutHeight())
            )
        }

        if (binding.rvContainer.adapter == null) {
            binding.rvContainer.adapter =
                SystemContentAdapter(requireContext(), arrayListOf()).apply {
                    mAdapter = this
                    setLoadResource(MainLoadResource())
                    initStatusView()
                    pageLoading()
                    bindAllListener(this@SystemContentFragment)
                }.proxy
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        LiveDataBus.instance.injectBus(this@SystemContentFragment)

        launchWhenResumed {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is SystemContentViewEvent.RequestFailed -> {
                        actionFailed(event.error)
                        mAdapter?.submitFailed()
                    }
                    is SystemContentViewEvent.NavigationContentTabEvent -> {
                        findNavController().navigate(
                            R.id.action_system_fragment_to_system_content_tab_fragment,
                            event.bundle
                        )
                    }
                }
            }
        }

        launchWhenStarted {
            viewModel.viewStates.collectState(
                SystemContentViewState::isLoading,
                SystemContentViewState::parentTabList
            ) { isLoading, data ->
                if (!isLoading) mAdapter?.submitSinglePage(data)
            }
        }
    }

    override fun onItemClick(view: View, entity: ParentTab, position: Int) {
        viewModel.dispatch(SystemContentViewAction.NavigationContentTab(entity))
    }

    override fun pageReload() {
        viewModel.dispatch(SystemContentViewAction.RequestData)
    }

    override fun itemReload() {}

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvContainer.adapter = null
        mAdapter = null
    }

    @InjectBus
    fun navigationEvent(event: NavigationSelectEvent) {
        if (event.title == getString(R.string.nav_system) && isResumed) {
            binding.rvContainer.smoothScrollToTop()
        }
    }
}