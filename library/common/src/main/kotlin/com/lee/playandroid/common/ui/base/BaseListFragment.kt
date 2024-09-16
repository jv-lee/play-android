package com.lee.playandroid.common.ui.base

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lee.playandroid.base.adapter.base.BaseViewAdapter
import com.lee.playandroid.base.adapter.extensions.bindAllListener
import com.lee.playandroid.base.adapter.extensions.unbindAllListener
import com.lee.playandroid.base.adapter.page.submitData
import com.lee.playandroid.base.adapter.page.submitFailed
import com.lee.playandroid.base.base.BaseBindingNavigationFragment
import com.lee.playandroid.base.uistate.LoadStatus
import com.lee.playandroid.base.uistate.UiStatePage
import com.lee.playandroid.base.uistate.collectCallback
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
abstract class BaseListFragment :
    BaseBindingNavigationFragment<FragmentBaseListBinding>(),
    SwipeRefreshLayout.OnRefreshListener,
    BaseViewAdapter.AutoLoadMoreListener,
    BaseViewAdapter.LoadErrorListener,
    BaseViewAdapter.OnItemClickListener<Content> {

    private lateinit var mAdapter: BaseViewAdapter<Content>

    abstract fun createAdapter(): BaseViewAdapter<Content>

    abstract fun requestContentList(@LoadStatus status: Int)

    abstract fun navigationDetails(content: Content)

    abstract fun dataFlow(): StateFlow<UiStatePage>

    override fun bindView() {
        if (mBinding.rvContainer.adapter == null) {
            createAdapter().apply {
                mAdapter = this
                bindRecyclerView(mBinding.rvContainer, loadStateEnable = false)
            }
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        // 列表数据更新
        launchWhenResumed {
            dataFlow().collectCallback<PageData<Content>>(
                success = {
                    mBinding.refreshLayout.isRefreshing = false
                    mAdapter.submitData(it, diff = true)
                },
                error = {
                    mBinding.refreshLayout.isRefreshing = false
                    mAdapter.submitFailed()
                    actionFailed(it)
                }
            )
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
        mBinding.refreshLayout.setOnRefreshListener(this)
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        mAdapter.unbindAllListener()
        mBinding.refreshLayout.setOnRefreshListener(null)
    }
}