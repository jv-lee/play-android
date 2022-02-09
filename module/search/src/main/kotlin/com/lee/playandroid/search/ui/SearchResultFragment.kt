package com.lee.playandroid.search.ui

import android.view.View
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.adapter.extensions.bindAllListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.arguments
import com.lee.library.extensions.binding
import com.lee.library.extensions.viewModelByFactory
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.observeState
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.search.R
import com.lee.playandroid.search.constants.Constants
import com.lee.playandroid.search.databinding.FragmentSearchResultBinding
import com.lee.playandroid.search.ui.adapter.SearchResultAdapter
import com.lee.playandroid.search.viewmodel.SearchResultViewModel

/**
 * @author jv.lee
 * @date 2021/11/22
 * @description
 */
class SearchResultFragment : BaseNavigationFragment(R.layout.fragment_search_result),
    BaseViewAdapter.AutoLoadMoreListener,
    BaseViewAdapter.LoadErrorListener,
    BaseViewAdapter.OnItemClickListener<Content> {

    private val searchKey by arguments<String>(Constants.ARG_PARAMS_SEARCH_KEY)

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

    override fun bindData() {
        viewModel.searchResultLive.observeState<PageData<Content>>(viewLifecycleOwner, success = {
            mAdapter?.submitData(it, diff = true)
        }, error = {
            mAdapter?.submitFailed()
            actionFailed(it)
        })
    }

    override fun onItemClick(view: View?, entity: Content?, position: Int) {
        entity?.apply {
            findNavController().navigateDetails(title, link, id, collect)
        }
    }

    override fun autoLoadMore() {
        viewModel.requestSearch(LoadStatus.LOAD_MORE)
    }

    override fun pageReload() {
        viewModel.requestSearch(LoadStatus.REFRESH)
    }

    override fun itemReload() {
        viewModel.requestSearch(LoadStatus.RELOAD)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvContainer.adapter = null
        mAdapter = null
    }
}