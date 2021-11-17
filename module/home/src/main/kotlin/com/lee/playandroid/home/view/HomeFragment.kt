package com.lee.playandroid.home.view

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.listener.LoadErrorListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.delayBackEvent
import com.lee.library.extensions.toast
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.observeState
import com.lee.playandroid.home.R
import com.lee.playandroid.home.bean.HomeContent
import com.lee.playandroid.home.databinding.FragmentHomeBinding
import com.lee.playandroid.home.view.adapter.ContentAdapter
import com.lee.playandroid.home.viewmodel.HomeViewModel
import com.lee.playandroid.library.common.entity.PageUiData
import com.lee.playandroid.library.common.ui.widget.OffsetItemDecoration
import com.lee.playandroid.router.navigateDetails

/**
 * @author jv.lee
 * @data 2021/11/2
 * @description 首页第一个Tab HomeFragment
 */
class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val viewModel by viewModels<HomeViewModel>()

    private val binding by binding(FragmentHomeBinding::bind)

    private lateinit var mAdapter: ContentAdapter

    override fun bindView() {
        delayBackEvent()

        mAdapter = ContentAdapter(requireContext(), arrayListOf())

        binding.rvContainer.addItemDecoration(OffsetItemDecoration(binding.toolbar.getToolbarLayoutHeight()))
        binding.rvContainer.adapter = mAdapter.proxy

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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvContainer.removeAllViews()
    }

}