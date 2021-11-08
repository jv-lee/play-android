package com.lee.playandroid.home.view

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lee.library.adapter.listener.LoadErrorListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.toast
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.observeState
import com.lee.pioneer.library.common.entity.PageUiData
import com.lee.pioneer.router.navigateDetails
import com.lee.playandroid.home.R
import com.lee.playandroid.home.bean.HomeContent
import com.lee.playandroid.home.databinding.FragmentHomeBinding
import com.lee.playandroid.home.view.adapter.ContentAdapter
import com.lee.playandroid.home.viewmodel.HomeViewModel

/**
 * @author jv.lee
 * @data 2021/11/2
 * @description 首页fragment
 */
class HomeFragment : BaseNavigationFragment(R.layout.fragment_home) {

    private val viewModel by viewModels<HomeViewModel>()

    private val binding by binding(FragmentHomeBinding::bind)

    private val mAdapter by lazy { ContentAdapter(requireContext(), arrayListOf()) }

    override fun bindView() {
        binding.rvContainer.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter.proxy
        }

        binding.refreshView.setOnRefreshListener {
            mAdapter.openLoadMore()
            viewModel.loadListData(LoadStatus.REFRESH)
        }

        mAdapter.apply {
            initStatusView()
            pageLoading()
            setAutoLoadMoreListener {
                viewModel.loadListData(LoadStatus.LOAD_MORE)
            }
            setLoadErrorListener(object : LoadErrorListener {
                override fun pageReload() {
                    viewModel.loadListData(LoadStatus.REFRESH)
                }

                override fun itemReload() {
                    viewModel.loadListData(LoadStatus.RELOAD)
                }
            })
            setOnItemClickListener { _, entity, _ ->
                entity?.content?.apply {
                    findNavController().navigateDetails(link)
                }
            }
        }
    }

    override fun bindData() {
        viewModel.contentListLive.observeState<PageUiData<HomeContent>>(this, success = {
            binding.refreshView.isRefreshing = false
            mAdapter.submitData(it, diff = true)
        }, error = {
            toast(it.toString())
            binding.refreshView.isRefreshing = false
            mAdapter.submitFailed()
        })
    }

}