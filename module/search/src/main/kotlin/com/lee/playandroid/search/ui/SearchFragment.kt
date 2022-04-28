package com.lee.playandroid.search.ui

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.page.submitSinglePage
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.launchAndRepeatWithViewLifecycle
import com.lee.library.mvi.collectState
import com.lee.library.tools.KeyboardTools.hideSoftInput
import com.lee.library.tools.KeyboardTools.parentTouchHideSoftInput
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.search.R
import com.lee.playandroid.search.databinding.FragmentSearchBinding
import com.lee.playandroid.search.ui.adapter.SearchHistoryAdapter
import com.lee.playandroid.search.ui.adapter.SearchHotAdapter
import com.lee.playandroid.search.viewmodel.SearchViewAction
import com.lee.playandroid.search.viewmodel.SearchViewEvent
import com.lee.playandroid.search.viewmodel.SearchViewModel
import com.lee.playandroid.search.viewmodel.SearchViewState
import kotlinx.coroutines.flow.collect

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
                        viewModel.dispatch(SearchViewAction.NavigationSearch(entity.key))
                    }
                }.proxy
        }

        if (binding.rvHistoryContainer.adapter == null) {
            binding.rvHistoryContainer.adapter =
                SearchHistoryAdapter(requireContext(), arrayListOf()).apply {
                    mHistoryAdapter = this
                    setOnItemClickListener { _, entity, _ ->
                        viewModel.dispatch(SearchViewAction.NavigationSearch(entity.key))
                    }
                    setOnItemChildClickListener({ _, entity, _ ->
                        viewModel.dispatch(SearchViewAction.DeleteHistory(entity.key))
                    }, R.id.iv_delete)
                }.proxy
        }

        binding.editQuery.setOnEditorActionListener { textView, actionId, _ ->
            val text = textView.text
            if (actionId == IME_ACTION_SEARCH && text.isNotEmpty()) {
                viewModel.dispatch(SearchViewAction.NavigationSearch(text.toString()))
            }
            return@setOnEditorActionListener false
        }

        binding.tvHistoryClear.setOnClickListener {
            viewModel.dispatch(SearchViewAction.ClearHistory)
        }
    }

    override fun bindData() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is SearchViewEvent.Navigation -> {
                        navigationResult(event.key)
                    }
                    is SearchViewEvent.FailedEvent -> {
                        actionFailed(event.error)
                    }
                }
            }
        }

        viewModel.viewStates.run {
            launchAndRepeatWithViewLifecycle {
                collectState(SearchViewState::searchHotList) {
                    mHotAdapter?.submitSinglePage(it)
                }
            }
            launchAndRepeatWithViewLifecycle {
                collectState(SearchViewState::searchHistoryList) {
                    viewEmptyVisible(it.isEmpty())
                    mHistoryAdapter?.submitSinglePage(it)
                }
            }
        }
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

        val bundle = Bundle()
        bundle.putString(SearchResultFragment.ARG_PARAMS_SEARCH_KEY, key)
        findNavController().navigate(R.id.action_search_fragment_to_search_result_fragment, bundle)
    }

    /**
     * empty布局显示隐藏
     */
    private fun viewEmptyVisible(visible: Boolean) {
        binding.rvHistoryContainer.visibility =
            if (visible) View.GONE else View.VISIBLE
        binding.tvHistoryEmpty.visibility =
            if (visible) View.VISIBLE else View.GONE
    }

}