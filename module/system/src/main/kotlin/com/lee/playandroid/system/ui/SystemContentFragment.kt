package com.lee.playandroid.system.ui

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.base.adapter.base.BaseViewAdapter
import com.lee.playandroid.base.adapter.extensions.bindAllListener
import com.lee.playandroid.base.adapter.page.submitFailed
import com.lee.playandroid.base.adapter.page.submitSinglePage
import com.lee.playandroid.base.base.BaseBindingNavigationFragment
import com.lee.playandroid.base.extensions.collectState
import com.lee.playandroid.base.extensions.findParentFragment
import com.lee.playandroid.base.extensions.smoothScrollToTop
import com.lee.playandroid.base.livedatabus.InjectBus
import com.lee.playandroid.base.livedatabus.LiveDataBus
import com.lee.playandroid.common.entity.NavigationSelectEvent
import com.lee.playandroid.common.entity.ParentTab
import com.lee.playandroid.common.extensions.actionFailed
import com.lee.playandroid.common.ui.widget.MainLoadResource
import com.lee.playandroid.common.ui.widget.OffsetItemDecoration
import com.lee.playandroid.system.R
import com.lee.playandroid.common.R as CR
import com.lee.playandroid.system.databinding.FragmentSystemContentBinding
import com.lee.playandroid.system.ui.adapter.SystemContentAdapter
import com.lee.playandroid.system.viewmodel.SystemContentViewEvent
import com.lee.playandroid.system.viewmodel.SystemContentViewIntent
import com.lee.playandroid.system.viewmodel.SystemContentViewModel
import com.lee.playandroid.system.viewmodel.SystemContentViewState

/**
 * 体系列表Fragment
 * @see SystemFragment 体系Fragment下第一个Tab
 * @author jv.lee
 * @date 2021/11/10
 */
class SystemContentFragment :
    BaseBindingNavigationFragment<FragmentSystemContentBinding>(),
    BaseViewAdapter.OnItemClickListener<ParentTab>,
    BaseViewAdapter.LoadErrorListener {

    private val viewModel by viewModels<SystemContentViewModel>()

    private var mAdapter: SystemContentAdapter? = null

    override fun bindView() {
        // 根据父Fragment toolbar高度设置ItemDecoration来控制显示间隔
        findParentFragment<SystemFragment>()?.parentBindingAction {
            mBinding.rvContainer.addItemDecoration(
                OffsetItemDecoration(toolbar.getToolbarLayoutHeight())
            )
        }

        if (mBinding.rvContainer.adapter == null) {
            SystemContentAdapter(requireContext()).apply {
                mAdapter = this
                bindRecyclerView(mBinding.rvContainer, MainLoadResource())
                bindAllListener(this@SystemContentFragment)
            }
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        LiveDataBus.instance.injectBus(this@SystemContentFragment)

        launchOnLifecycle {
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
        viewModel.dispatch(SystemContentViewIntent.NavigationContentTab(entity))
    }

    override fun pageReload() {
        viewModel.dispatch(SystemContentViewIntent.RequestData)
    }

    override fun itemReload() {}

    override fun onDestroyView() {
        mBinding.rvContainer.adapter = null
        mAdapter = null
        super.onDestroyView()
    }

    @InjectBus
    fun navigationEvent(event: NavigationSelectEvent) {
        if (event.title == getString(CR.string.nav_system) && isResumed) {
            mBinding.rvContainer.smoothScrollToTop()
        }
    }
}