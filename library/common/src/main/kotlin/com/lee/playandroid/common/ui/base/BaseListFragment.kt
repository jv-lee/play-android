package com.lee.playandroid.common.ui.base

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lee.playandroid.base.adapter.base.BaseViewAdapter
import com.lee.playandroid.base.adapter.extensions.bindAllListener
import com.lee.playandroid.base.adapter.extensions.unbindAllListener
import com.lee.playandroid.base.adapter.page.submitData
import com.lee.playandroid.base.adapter.page.submitFailed
import com.lee.playandroid.base.base.BaseNavigationFragment
import com.lee.playandroid.base.extensions.binding
import com.lee.playandroid.base.viewstate.LoadStatus
import com.lee.playandroid.base.viewstate.UiStatePage
import com.lee.playandroid.base.viewstate.collectState
import com.lee.playandroid.common.R
import com.lee.playandroid.common.databinding.FragmentBaseListBinding
import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.entity.PageData
import com.lee.playandroid.common.extensions.actionFailed
import kotlinx.coroutines.flow.StateFlow

/**
 * 列表fragment通用类
 * @author jv.lee
 * @date 2021/11/8
 */
abstract class BaseListFragment : BaseNavigationFragment(R.layout.fragment_base_list),
    SwipeRefreshLayout.OnRefreshListener,
    BaseViewAdapter.AutoLoadMoreListener,
    BaseViewAdapter.LoadErrorListener,
    BaseViewAdapter.OnItemClickListener<Content> {

    private val binding by binding(FragmentBaseListBinding::bind)

    private lateinit var mAdapter: BaseViewAdapter<Content>

    abstract fun createAdapter(): BaseViewAdapter<Content>

    abstract fun requestContentList(@LoadStatus status: Int)

    abstract fun navigationDetails(content: Content)

    abstract fun dataFlow(): StateFlow<UiStatePage>

    open fun findBinding() = binding

    override fun bindView() {
        if (binding.rvContainer.adapter == null) {
            binding.rvContainer.adapter = createAdapter().apply {
                mAdapter = this
                initStatusView()
                pageLoading()
            }.proxy
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        //列表数据更新
        launchWhenResumed {
            dataFlow().collectState<PageData<Content>>(success = {
                binding.refreshLayout.isRefreshing = false
                mAdapter.submitData(it, diff = true)
            }, error = {
                binding.refreshLayout.isRefreshing = false
                mAdapter.submitFailed()
                actionFailed(it)
            })
        }
    }

    override fun lazyLoad() {
        super.lazyLoad()
        requestContentList(LoadStatus.INIT)
    }

    override fun onRefresh() {
        requestContentList(LoadStatus.REFRESH)
    }

    override fun autoLoadMore() {
        requestContentList(LoadStatus.LOAD_MORE)
    }

    override fun pageReload() {
        requestContentList(LoadStatus.REFRESH)
    }

    override fun itemReload() {
        requestContentList(LoadStatus.RELOAD)
    }

    override fun onItemClick(view: View, entity: Content, position: Int) {
        navigationDetails(entity)
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        mAdapter.bindAllListener(this@BaseListFragment)
        binding.refreshLayout.setOnRefreshListener(this)
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        mAdapter.unbindAllListener()
        binding.refreshLayout.setOnRefreshListener(null)
    }

}