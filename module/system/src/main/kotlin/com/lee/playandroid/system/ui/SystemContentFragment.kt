package com.lee.playandroid.system.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.adapter.page.submitFailed
import com.lee.library.adapter.page.submitSinglePage
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.findParentFragment
import com.lee.library.extensions.smoothScrollToTop
import com.lee.library.livedatabus.InjectBus
import com.lee.library.livedatabus.LiveDataBus
import com.lee.library.mvvm.ui.observeState
import com.lee.playandroid.library.common.entity.NavigationSelectEvent
import com.lee.playandroid.library.common.entity.ParentTab
import com.lee.playandroid.library.common.entity.Tab
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.library.common.ui.widget.OffsetItemDecoration
import com.lee.playandroid.system.R
import com.lee.playandroid.system.constants.Constants
import com.lee.playandroid.system.databinding.FragmentSystemContentBinding
import com.lee.playandroid.system.ui.adapter.SystemContentAdapter
import com.lee.playandroid.system.viewmodel.SystemContentViewModel

/**
 * @author jv.lee
 * @date 2021/11/10
 * @description  体系列表Fragment
 * @see SystemFragment 体系Fragment下第一个Tab
 */
class SystemContentFragment : BaseFragment(R.layout.fragment_system_content) {

    private val viewModel by viewModels<SystemContentViewModel>()

    private val binding by binding(FragmentSystemContentBinding::bind)

    private lateinit var mAdapter: SystemContentAdapter

    override fun bindView() {
        mAdapter = SystemContentAdapter(requireContext(), arrayListOf())
        binding.rvContainer.adapter = mAdapter.proxy

        //根据父Fragment toolbar高度设置ItemDecoration来控制显示间隔
        findParentFragment<SystemFragment>()?.parentBindingAction {
            binding.rvContainer.addItemDecoration(OffsetItemDecoration(it.toolbar.getToolbarLayoutHeight()))
        }

        mAdapter.apply {
            mAdapter.initStatusView()
            mAdapter.pageLoading()

            setAutoLoadMoreListener {
                viewModel.requestParentTab()
            }

            setLoadErrorListener(object : BaseViewAdapter.LoadErrorListener {
                override fun pageReload() {
                    viewModel.requestParentTab()
                }

                override fun itemReload() {}
            })
            setOnItemClickListener { _, entity, _ ->
                navigationToContentTab(entity)
            }
        }
    }

    override fun bindData() {
        LiveDataBus.getInstance().injectBus(this)

        viewModel.parentTabLive.observeState<List<ParentTab>>(this, success = { data ->
            mAdapter.submitSinglePage(data)
        }, error = {
            mAdapter.submitFailed()
            actionFailed(it)
        })
    }

    /**
     * 导航至目标页面
     * @param item 数据item title:item.name ,data:item.children
     * @see ContentTabFragment
     */
    private fun navigationToContentTab(item: ParentTab) {
        val data = arrayListOf<Tab>().apply { addAll(item.children) }

        val bundle = Bundle()
        bundle.putString(Constants.ARG_PARAMS_CONTENT_TAB_TITLE, item.name)
        bundle.putParcelableArrayList(Constants.ARG_PARAMS_CONTENT_TAB_DATA, data)

        findNavController().navigate(R.id.action_system_fragment_to_content_tab_fragment, bundle)
    }

    @InjectBus(NavigationSelectEvent.key, isActive = true)
    fun navigationEvent(event: NavigationSelectEvent) {
        if (event.title == getString(R.string.nav_system) && isResumed) {
            binding.rvContainer.smoothScrollToTop()
        }
    }

}