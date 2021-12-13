package com.lee.playandroid.search.ui

import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.listener.LoadErrorListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseFragment
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
class SearchResultFragment : BaseFragment(R.layout.fragment_search_result) {

    private val searchKey by arguments<String>(Constants.ARG_PARAMS_SEARCH_KEY)

    private val viewModel by viewModelByFactory<SearchResultViewModel>()

    private val binding by binding(FragmentSearchResultBinding::bind)

    private lateinit var mAdapter: SearchResultAdapter

    override fun bindView() {
        binding.toolbar.setTitleText(searchKey)

        binding.rvContainer.adapter = SearchResultAdapter(requireContext(), arrayListOf()).apply {
            mAdapter = this

            initStatusView()
            pageLoading()
            setAutoLoadMoreListener {
                viewModel.requestSearch(LoadStatus.LOAD_MORE)
            }
            setLoadErrorListener(object : LoadErrorListener {
                override fun pageReload() {
                    viewModel.requestSearch(LoadStatus.REFRESH)
                }

                override fun itemReload() {
                    viewModel.requestSearch(LoadStatus.RELOAD)
                }
            })
            setOnItemClickListener { _, entity, _ ->
                entity?.apply {
                    findNavController().navigateDetails(id,title, link,collect)
                }
            }
        }.proxy

    }

    override fun bindData() {
        viewModel.searchResultLive.observeState<PageData<Content>>(this, success = {
            mAdapter.submitData(it, diff = true)
        }, error = {
            mAdapter.submitFailed()
            actionFailed(it)
        })
    }
}