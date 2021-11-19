package com.lee.playandroid.search.ui

import androidx.fragment.app.viewModels
import com.lee.library.adapter.page.submitSinglePage
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.toast
import com.lee.library.mvvm.ui.observeState
import com.lee.playandroid.search.R
import com.lee.playandroid.search.databinding.FragmentSearchBinding
import com.lee.playandroid.search.helper.SearchHot
import com.lee.playandroid.search.ui.adapter.SearchHotAdapter
import com.lee.playandroid.search.viewmodel.SearchViewModel

/**
 * @author jv.lee
 * @date 2021/11/19
 * @description
 */
class SearchFragment : BaseFragment(R.layout.fragment_search) {

    private val viewModel by viewModels<SearchViewModel>()

    private val binding by binding(FragmentSearchBinding::bind)

    private lateinit var mHotAdapter: SearchHotAdapter

    override fun bindView() {
        binding.rvHotContainer.adapter = SearchHotAdapter(requireContext(), arrayListOf()).apply {
            mHotAdapter = this

            setOnItemClickListener { _, entity, _ ->
                toast(entity.key)
            }
        }.proxy
    }

    override fun bindData() {
        viewModel.searchHotLive.observeState<List<SearchHot>>(this, success = {
            mHotAdapter.submitSinglePage(it)
        }, error = {})
    }

}