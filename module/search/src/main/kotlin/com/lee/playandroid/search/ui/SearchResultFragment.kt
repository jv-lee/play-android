package com.lee.playandroid.search.ui

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.adapter.extensions.bindAllListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.arguments
import com.lee.library.extensions.binding
import com.lee.library.extensions.viewModelByFactory
import com.lee.library.viewstate.LoadStatus
import com.lee.library.viewstate.collectState
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.search.R
import com.lee.playandroid.search.databinding.FragmentSearchResultBinding
import com.lee.playandroid.search.ui.adapter.SearchResultAdapter
import com.lee.playandroid.search.viewmodel.SearchResultViewAction
import com.lee.playandroid.search.viewmodel.SearchResultViewModel

/**
 * @author jv.lee
 * @date 2021/11/22
 * @description 搜索结果列表
 */
class SearchResultFragment : BaseNavigationFragment(R.layout.fragment_search_result),
    BaseViewAdapter.AutoLoadMoreListener,
    BaseViewAdapter.LoadErrorListener,
    BaseViewAdapter.OnItemClickListener<Content> {

    companion object {
        const val ARG_PARAMS_SEARCH_KEY = "searchKey"
    }

    private val searchKey by arguments<String>(ARG_PARAMS_SEARCH_KEY)

    private val viewModel by viewModelByFactory<SearchResultViewModel>()

    private val binding by binding(FragmentSearchResultBinding::bind)

    private var mAdapter: SearchResultAdapter? = null

    override fun bindView() {
        binding.toolbar.setTitleText(searchKey)

        if (binding.rvContainer.adapter == null) {
            binding.rvContainer.adapter =
                SearchResultAdapter(requireContext(), arrayListOf()).apply {
                    mAdapter = this

                    initStatusView()
                    pageLoading()
                    bindAllListener(this@SearchResultFragment)
                }.proxy
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchWhenResumed {
            viewModel.searchResultFlow.collectState<PageData<Content>>(success = {
                mAdapter?.submitData(it, diff = true)
            }, error = {
                mAdapter?.submitFailed()
                actionFailed(it)
            })
        }
    }

    override fun onItemClick(view: View?, entity: Content?, position: Int) {
        entity?.apply {
            findNavController().navigateDetails(title, link, id, collect)
        }
    }

    override fun autoLoadMore() {
        viewModel.dispatch(SearchResultViewAction.RequestPage(LoadStatus.LOAD_MORE))
    }

    override fun pageReload() {
        viewModel.dispatch(SearchResultViewAction.RequestPage(LoadStatus.REFRESH))
    }

    override fun itemReload() {
        viewModel.dispatch(SearchResultViewAction.RequestPage(LoadStatus.RELOAD))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvContainer.adapter = null
        mAdapter = null
    }
}