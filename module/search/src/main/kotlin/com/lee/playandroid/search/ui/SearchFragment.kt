package com.lee.playandroid.search.ui

import android.os.Bundle
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.page.submitSinglePage
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.toast
import com.lee.library.mvvm.ui.observeState
import com.lee.playandroid.search.R
import com.lee.playandroid.search.constants.Constants
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
        mHotAdapter = SearchHotAdapter(requireContext(), arrayListOf())
        binding.rvHotContainer.adapter = mHotAdapter.proxy

        mHotAdapter.setOnItemClickListener { _, entity, _ ->
            navigationResult(entity.key)
        }

        binding.editQuery.setOnEditorActionListener { textView, actionId, _ ->
            val text = textView.text
            if (actionId == IME_ACTION_SEARCH && text.isNotEmpty()) {
                navigationResult(text.toString())
            }
            return@setOnEditorActionListener false
        }
    }

    override fun bindData() {
        viewModel.searchHotLive.observeState<List<SearchHot>>(this, success = {
            mHotAdapter.submitSinglePage(it)
        }, error = {
            toast(it.message)
        })
    }

    /**
     * 导航到搜索结果页
     * @param key 搜索key
     */
    private fun navigationResult(key: String) {
        val bundle = Bundle()
        bundle.putString(Constants.ARG_PARAMS_SEARCH_KEY, key)
        findNavController().navigate(R.id.action_search_to_result, bundle)
    }

}