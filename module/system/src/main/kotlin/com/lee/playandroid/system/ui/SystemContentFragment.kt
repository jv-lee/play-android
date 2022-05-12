package com.lee.playandroid.system.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.adapter.extensions.bindAllListener
import com.lee.library.adapter.page.submitFailed
import com.lee.library.adapter.page.submitSinglePage
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.findParentFragment
import com.lee.library.extensions.launchAndRepeatWithViewLifecycle
import com.lee.library.extensions.smoothScrollToTop
import com.lee.library.livedatabus.InjectBus
import com.lee.library.livedatabus.LiveDataBus
import com.lee.library.viewstate.collectState
import com.lee.playandroid.library.common.entity.NavigationSelectEvent
import com.lee.playandroid.library.common.entity.ParentTab
import com.lee.playandroid.library.common.entity.Tab
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.library.common.ui.widget.MainLoadResource
import com.lee.playandroid.library.common.ui.widget.OffsetItemDecoration
import com.lee.playandroid.system.R
import com.lee.playandroid.system.databinding.FragmentSystemContentBinding
import com.lee.playandroid.system.ui.adapter.SystemContentAdapter
import com.lee.playandroid.system.viewmodel.SystemContentViewAction
import com.lee.playandroid.system.viewmodel.SystemContentViewEvent
import com.lee.playandroid.system.viewmodel.SystemContentViewModel
import com.lee.playandroid.system.viewmodel.SystemContentViewState
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/11/10
 * @description  体系列表Fragment
 * @see SystemFragment 体系Fragment下第一个Tab
 */
class SystemContentFragment : BaseNavigationFragment(R.layout.fragment_system_content),
    BaseViewAdapter.OnItemClickListener<ParentTab>,
    BaseViewAdapter.LoadErrorListener {

    private val viewModel by viewModels<SystemContentViewModel>()

    private val binding by binding(FragmentSystemContentBinding::bind)

    private var mAdapter: SystemContentAdapter? = null

    override fun bindView() {
        //根据父Fragment toolbar高度设置ItemDecoration来控制显示间隔
        findParentFragment<SystemFragment>()?.parentBindingAction {
            binding.rvContainer.addItemDecoration(OffsetItemDecoration(it.toolbar.getToolbarLayoutHeight()))
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
        LiveDataBus.getInstance().injectBus(this@SystemContentFragment)

        launchWhenResumed {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is SystemContentViewEvent.RequestFailed -> {
                        actionFailed(event.error)
                        mAdapter?.submitFailed()
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
        navigationToContentTab(entity)
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

    /**
     * 导航至目标页面
     * @param item 数据item title:item.name ,data:item.children
     * @see SystemContentTabFragment
     */
    private fun navigationToContentTab(item: ParentTab) {
        val data = arrayListOf<Tab>().apply { addAll(item.children) }

        val bundle = Bundle()
        bundle.putString(SystemContentTabFragment.ARG_PARAMS_TAB_TITLE, item.name)
        bundle.putParcelableArrayList(SystemContentTabFragment.ARG_PARAMS_TAB_DATA, data)

        findNavController().navigate(
            R.id.action_system_fragment_to_system_content_tab_fragment,
            bundle
        )
    }

    @InjectBus(NavigationSelectEvent.key, isActive = true)
    fun navigationEvent(event: NavigationSelectEvent) {
        if (event.title == getString(R.string.nav_system) && isResumed) {
            binding.rvContainer.smoothScrollToTop()
        }
    }

}