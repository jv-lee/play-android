package com.lee.playandroid.search.ui

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.base.adapter.base.BaseViewAdapter
import com.lee.playandroid.base.adapter.extensions.bindAllListener
import com.lee.playandroid.base.adapter.page.submitData
import com.lee.playandroid.base.adapter.page.submitFailed
import com.lee.playandroid.base.base.BaseBindingNavigationFragment
import com.lee.playandroid.base.extensions.collectState
import com.lee.playandroid.base.uistate.LoadStatus
import com.lee.playandroid.base.uistate.collectCallback
import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.entity.PageData
import com.lee.playandroid.common.extensions.actionFailed
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.search.databinding.FragmentSearchResultBinding
import com.lee.playandroid.search.ui.adapter.SearchResultAdapter
import com.lee.playandroid.search.viewmodel.SearchResultViewIntent
import com.lee.playandroid.search.viewmodel.SearchResultViewModel
import com.lee.playandroid.search.viewmodel.SearchResultViewState

/**
 * 搜索结果列表
 * @author jv.lee
 * @date 2021/11/22
 */
class SearchResultFragment :
    BaseBindingNavigationFragment<FragmentSearchResultBinding>(),
    BaseViewAdapter.AutoLoadMoreListener,
    BaseViewAdapter.LoadErrorListener,
    BaseViewAdapter.OnItemClickListener<Content> {

    companion object {
        /** 搜索键名 */
        const val ARG_PARAMS_SEARCH_KEY = "searchKey"
    }

    private val viewModel by viewModels<SearchResultViewModel>()

    private var mAdapter: SearchResultAdapter? = null

    override fun bindView() {
        // 搜索结果列表适配器设置
        if (mBinding.rvContainer.adapter == null) {
            mBinding.rvContainer.adapter =
                SearchResultAdapter(requireContext()).apply {
                    mAdapter = this

                    initStatusView()
                    pageLoading()
                    bindAllListener(this@SearchResultFragment)
                }.getProxy()
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchWhenResumed {
            // 监听搜索key绑定到toolbar.title
            viewModel.viewStates.collectState(SearchResultViewState::title) {
                mBinding.toolbar.setTitleText(it)
            }
        }

        launchWhenResumed {
            // 监听搜索结果列表数据绑定
            viewModel.searchResultFlow.collectCallback<PageData<Content>>(
                success = {
                    mAdapter?.submitData(it, diff = true)
                },
                error = {
                    mAdapter?.submitFailed()
                    actionFailed(it)
                }
            )
        }
    }

    override fun onItemClick(view: View, entity: Content, position: Int) {
        entity.run { findNavController().navigateDetails(title, link, id, collect) }
    }

    override fun autoLoadMore() {
        viewModel.dispatch(SearchResultViewIntent.RequestPage(LoadStatus.LOAD_MORE))
    }

    override fun pageReload() {
        viewModel.dispatch(SearchResultViewIntent.RequestPage(LoadStatus.REFRESH))
    }

    override fun itemReload() {
        viewModel.dispatch(SearchResultViewIntent.RequestPage(LoadStatus.RELOAD))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.rvContainer.adapter = null
        mAdapter = null
    }
}