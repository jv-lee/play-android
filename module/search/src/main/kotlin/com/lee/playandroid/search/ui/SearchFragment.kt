package com.lee.playandroid.search.ui

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.page.submitSinglePage
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.binding
import com.lee.library.mvvm.ui.observeState
import com.lee.library.tools.KeyboardTools.hideSoftInput
import com.lee.library.tools.KeyboardTools.parentTouchHideSoftInput
import com.lee.playandroid.library.common.entity.SearchHistory
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.search.R
import com.lee.playandroid.search.constants.Constants
import com.lee.playandroid.search.databinding.FragmentSearchBinding
import com.lee.playandroid.search.helper.SearchHot
import com.lee.playandroid.search.ui.adapter.SearchHistoryAdapter
import com.lee.playandroid.search.ui.adapter.SearchHotAdapter
import com.lee.playandroid.search.viewmodel.SearchViewModel

/**
 * @author jv.lee
 * @date 2021/11/19
 * @description 搜索页面
 */
class SearchFragment : BaseNavigationFragment(R.layout.fragment_search) {

    private val viewModel by viewModels<SearchViewModel>()

    private val binding by binding(FragmentSearchBinding::bind)

    private var mHotAdapter: SearchHotAdapter? = null
    private var mHistoryAdapter: SearchHistoryAdapter? = null

    override fun bindView() {
        // 设置点击空白区域隐藏软键盘
        requireActivity().parentTouchHideSoftInput(binding.root)

        if (binding.rvHotContainer.adapter == null) {
            binding.rvHotContainer.adapter =
                SearchHotAdapter(requireContext(), arrayListOf()).apply {
                    mHotAdapter = this
                    setOnItemClickListener { _, entity, _ ->
                        navigationResult(entity.key)
                    }
                }.proxy
        }

        if (binding.rvHistoryContainer.adapter == null) {
            binding.rvHistoryContainer.adapter =
                SearchHistoryAdapter(requireContext(), arrayListOf()).apply {
                    mHistoryAdapter = this
                    setOnItemClickListener { _, entity, _ ->
                        navigationResult(entity.key)
                    }
                    setOnItemChildClickListener({ _, entity, _ ->
                        viewModel.deleteSearchHistory(entity.key)
                    }, R.id.iv_delete)
                }.proxy
        }

        binding.editQuery.setOnEditorActionListener { textView, actionId, _ ->
            val text = textView.text
            if (actionId == IME_ACTION_SEARCH && text.isNotEmpty()) {
                navigationResult(text.toString())
            }
            return@setOnEditorActionListener false
        }

        binding.tvHistoryClear.setOnClickListener {
            viewModel.clearSearchHistory()
        }
    }

    override fun bindData() {
        viewModel.searchHotLive.observeState<List<SearchHot>>(viewLifecycleOwner, success = {
            mHotAdapter?.submitSinglePage(it)
        }, error = {
            actionFailed(it)
        })

        viewModel.searchHistoryLive.observeState<List<SearchHistory>>(this, success = {
            binding.rvHistoryContainer.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            binding.tvHistoryEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            mHistoryAdapter?.submitSinglePage(it)
        }, error = {
            actionFailed(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvHotContainer.adapter = null
        binding.rvHistoryContainer.adapter = null
        mHistoryAdapter = null
        mHistoryAdapter = null
    }

    /**
     * 导航到搜索结果页
     * @param key 搜索key
     */
    private fun navigationResult(key: String) {
        //隐藏键盘
        requireActivity().hideSoftInput()

        //存储搜索记录
        viewModel.addSearchHistory(key)

        val bundle = Bundle()
        bundle.putString(Constants.ARG_PARAMS_SEARCH_KEY, key)
        findNavController().navigate(R.id.action_search_fragment_to_search_result_fragment, bundle)
    }

}