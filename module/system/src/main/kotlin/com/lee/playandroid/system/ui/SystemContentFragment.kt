package com.lee.playandroid.system.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lee.library.adapter.listener.LoadErrorListener
import com.lee.library.adapter.page.submitFailed
import com.lee.library.adapter.page.submitSinglePage
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.toast
import com.lee.library.mvvm.ui.observeState
import com.lee.playandroid.library.common.entity.ParentTab
import com.lee.playandroid.library.common.entity.Tab
import com.lee.playandroid.system.R
import com.lee.playandroid.system.constants.Constants
import com.lee.playandroid.system.databinding.FragmentSystemContentBinding
import com.lee.playandroid.system.ui.adapter.SystemContentAdapter
import com.lee.playandroid.system.viewmodel.SystemContentViewModel

/**
 * @author jv.lee
 * @data 2021/11/10
 * @description
 */
class SystemContentFragment : BaseFragment(R.layout.fragment_system_content) {

    private val viewModel by viewModels<SystemContentViewModel>()

    private val binding by binding(FragmentSystemContentBinding::bind)

    private lateinit var mAdapter: SystemContentAdapter

    override fun bindView() {
        mAdapter = SystemContentAdapter(requireContext(), arrayListOf())

        binding.rvContainer.layoutManager = LinearLayoutManager(requireContext())
        binding.rvContainer.adapter = mAdapter.proxy

        mAdapter.apply {
            initStatusView()
            pageLoading()
            setAutoLoadMoreListener {
                viewModel.requestParentTab()
            }

            setLoadErrorListener(object : LoadErrorListener {
                override fun pageReload() {
                    viewModel.requestParentTab()
                }

                override fun itemReload() {}
            })
            setOnItemClickListener { _, entity, _ ->
                findNavController()
                    .navigate(R.id.action_systemContent_to_contentTab, Bundle().apply {
                        putString(Constants.CONTENT_TAB_TITLE_KEY, entity.name)
                        putParcelableArrayList(Constants.CONTENT_TAB_DATA_KEY,
                            arrayListOf<Tab>().apply {
                                addAll(entity.children)
                            })
                    })
            }
        }
    }

    override fun bindData() {
        viewModel.parentTabLive.observeState<List<ParentTab>>(this, success = { data ->
            mAdapter.submitSinglePage(data)
        }, error = {
            toast(it.message)
            mAdapter.submitFailed()
        })
    }

    override fun lazyLoad() {
        viewModel.requestParentTab()
    }

}