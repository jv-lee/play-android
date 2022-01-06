package com.lee.playandroid.library.common.ui

import androidx.lifecycle.LiveData
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.adapter.listener.LoadErrorListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.binding
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.observeState
import com.lee.playandroid.library.common.R
import com.lee.playandroid.library.common.databinding.FragmentBaseListBinding
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.extensions.actionFailed

/**
 * @author jv.lee
 * @date 2021/11/8
 * @description
 */
abstract class BaseListFragment : BaseNavigationFragment(R.layout.fragment_base_list) {

    private val binding by binding(FragmentBaseListBinding::bind)

    private lateinit var mAdapter: BaseViewAdapter<Content>

    abstract fun createAdapter(): BaseViewAdapter<Content>

    abstract fun requestContentList(@LoadStatus status: Int)

    abstract fun navigationDetails(content: Content)

    abstract fun dataObserveState(): LiveData<UiState>

    open fun findBinding() = binding

    override fun bindView() {
        binding.rvContainer.adapter = createAdapter().apply {
            mAdapter = this
            initStatusView()
            pageLoading()
            setAutoLoadMoreListener {
                requestContentList(LoadStatus.LOAD_MORE)
            }

            setLoadErrorListener(object : LoadErrorListener {
                override fun pageReload() {
                    requestContentList(LoadStatus.REFRESH)
                }

                override fun itemReload() {
                    requestContentList(LoadStatus.RELOAD)
                }
            })

            setOnItemClickListener { _, entity, _ ->
                navigationDetails(entity)
            }
        }.proxy

        binding.refreshLayout.setOnRefreshListener {
            mAdapter.openLoadMore()
            requestContentList(LoadStatus.REFRESH)
        }
    }

    override fun bindData() {
        //列表数据更新
        dataObserveState().observeState<PageData<Content>>(viewLifecycleOwner,
            success = {
                binding.refreshLayout.isRefreshing = false
                mAdapter.submitData(it, diff = true)
            },
            error = {
                binding.refreshLayout.isRefreshing = false
                mAdapter.submitFailed()
                actionFailed(it)
            })
    }

    override fun lazyLoad() {
        super.lazyLoad()
        requestContentList(LoadStatus.INIT)
    }

}