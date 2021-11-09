package com.lee.playandroid.project.ui

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lee.library.adapter.listener.LoadErrorListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.toast
import com.lee.library.extensions.viewModelByFactory
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.observeState
import com.lee.pioneer.library.common.entity.Content
import com.lee.pioneer.library.common.entity.PageData
import com.lee.pioneer.router.navigateDetails
import com.lee.playandroid.project.R
import com.lee.playandroid.project.databinding.FragmentProjectListBinding
import com.lee.playandroid.project.ui.adapter.ProjectListAdapter
import com.lee.playandroid.project.viewmodel.ProjectListViewModel

/**
 * @author jv.lee
 * @data 2021/11/9
 * @description
 */
class ProjectListFragment : BaseFragment(R.layout.fragment_project_list) {

    companion object {
        const val ARG_PARAMS_ID = "arg_params_id"

        fun newInstance(id: Long) = ProjectListFragment().apply {
            arguments = Bundle().apply { putLong(ARG_PARAMS_ID, id) }
        }
    }

    private val viewModel by viewModelByFactory<ProjectListViewModel>()

    private val binding by binding(FragmentProjectListBinding::bind)

    private val mAdapter by lazy { ProjectListAdapter(requireContext(), arrayListOf()) }

    override fun bindView() {
        binding.rvContainer.layoutManager = LinearLayoutManager(requireContext())
        binding.rvContainer.adapter = mAdapter.proxy

        binding.refreshLayout.setOnRefreshListener {
            mAdapter.openLoadMore()
            viewModel.requestContentList(LoadStatus.REFRESH)
        }

        mAdapter.apply {
            initStatusView()
            pageLoading()
            setAutoLoadMoreListener {
                viewModel.requestContentList(LoadStatus.LOAD_MORE)
            }

            setLoadErrorListener(object : LoadErrorListener {
                override fun pageReload() {
                    viewModel.requestContentList(LoadStatus.REFRESH)
                }

                override fun itemReload() {
                    viewModel.requestContentList(LoadStatus.RELOAD)
                }
            })

            setOnItemClickListener { _, entity, _ ->
                findNavController().navigateDetails(entity.link)
            }
        }
    }

    override fun bindData() {
        //列表数据更新
        viewModel.contentListLive.observeState<PageData<Content>>(requireParentFragment(),
            success = {
                binding.refreshLayout.isRefreshing = false
                mAdapter.submitData(it, diff = true)
            },
            error = {
                toast(it.message)
                binding.refreshLayout.isRefreshing = false
                mAdapter.submitFailed()
            })
    }

    override fun lazyLoad() {
        viewModel.requestContentList(LoadStatus.INIT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvContainer.removeAllViews()
    }
}