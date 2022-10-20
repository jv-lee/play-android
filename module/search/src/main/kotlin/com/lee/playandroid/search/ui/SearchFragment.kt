package com.lee.playandroid.search.ui

import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.base.adapter.page.submitSinglePage
import com.lee.playandroid.base.base.BaseNavigationFragment
import com.lee.playandroid.base.extensions.binding
import com.lee.playandroid.base.tools.SystemBarTools.hasSoftInputShow
import com.lee.playandroid.base.tools.SystemBarTools.hideSoftInput
import com.lee.playandroid.base.tools.SystemBarTools.parentTouchHideSoftInput
import com.lee.playandroid.base.viewstate.collectState
import com.lee.playandroid.common.extensions.actionFailed
import com.lee.playandroid.search.R
import com.lee.playandroid.search.databinding.FragmentSearchBinding
import com.lee.playandroid.search.ui.adapter.SearchHistoryAdapter
import com.lee.playandroid.search.ui.adapter.SearchHotAdapter
import com.lee.playandroid.search.viewmodel.SearchViewAction
import com.lee.playandroid.search.viewmodel.SearchViewEvent
import com.lee.playandroid.search.viewmodel.SearchViewModel
import com.lee.playandroid.search.viewmodel.SearchViewState

/**
 * 搜索页面
 * @author jv.lee
 * @date 2021/11/19
 */
class SearchFragment : BaseNavigationFragment(R.layout.fragment_search) {

    private val viewModel by viewModels<SearchViewModel>()

    private val binding by binding(FragmentSearchBinding::bind)

    private var mHotAdapter: SearchHotAdapter? = null
    private var mHistoryAdapter: SearchHistoryAdapter? = null

    override fun bindView() {
        // 设置点击空白区域隐藏软键盘
        requireActivity().window.parentTouchHideSoftInput()

        // 清除历史记录点击事件
        binding.tvHistoryClear.setOnClickListener {
            viewModel.dispatch(SearchViewAction.ClearHistory)
        }

        // 搜索输入框内容监听
        binding.editQuery.setOnEditorActionListener { textView, actionId, _ ->
            val text = textView.text
            if (actionId == IME_ACTION_SEARCH && text.isNotEmpty()) {
                viewModel.dispatch(SearchViewAction.NavigationSearchResult(text.toString()))
            }
            return@setOnEditorActionListener false
        }

        // 搜索热词列表适配器设置
        if (binding.rvHotContainer.adapter == null) {
            binding.rvHotContainer.adapter =
                SearchHotAdapter(requireContext(), arrayListOf()).apply {
                    mHotAdapter = this
                    setOnItemClickListener { _, entity, _ ->
                        viewModel.dispatch(SearchViewAction.NavigationSearchResult(entity.key))
                    }
                }.proxy
        }

        // 搜索历史列表适配器设置
        if (binding.rvHistoryContainer.adapter == null) {
            binding.rvHistoryContainer.adapter =
                SearchHistoryAdapter(requireContext(), arrayListOf()).apply {
                    mHistoryAdapter = this
                    setOnItemClickListener { _, entity, _ ->
                        viewModel.dispatch(SearchViewAction.NavigationSearchResult(entity.key))
                    }
                    setOnItemChildClickListener({ _, entity, _ ->
                        viewModel.dispatch(SearchViewAction.DeleteHistory(entity.key))
                    }, R.id.iv_delete)
                }.proxy
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchWhenResumed {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    // 搜索结果提交页面导航处理
                    is SearchViewEvent.NavigationSearchResultEvent -> {
                        findNavController().navigate(
                            R.id.action_search_fragment_to_search_result_fragment,
                            event.bundle
                        )
                    }
                    // 页面错误统一处理
                    is SearchViewEvent.FailedEvent -> {
                        actionFailed(event.error)
                    }
                }
            }
        }

        viewModel.viewStates.run {
            launchWhenResumed {
                // 监听搜索热词数据绑定回调
                collectState(SearchViewState::searchHotList) {
                    mHotAdapter?.submitSinglePage(it)
                }
            }
            launchWhenResumed {
                // 监听搜索历史数据绑定回调
                collectState(SearchViewState::searchHistoryList) {
                    viewEmptyVisible(it.isEmpty())
                    mHistoryAdapter?.submitSinglePage(it)
                }
            }
        }
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        if (requireActivity().window.hasSoftInputShow()) {
            requireActivity().window.hideSoftInput()
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
     * empty布局显示隐藏
     */
    private fun viewEmptyVisible(visible: Boolean) {
        binding.rvHistoryContainer.visibility =
            if (visible) View.GONE else View.VISIBLE
        binding.tvHistoryEmpty.visibility =
            if (visible) View.VISIBLE else View.GONE
    }

}