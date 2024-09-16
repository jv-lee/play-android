package com.lee.playandroid.search.ui

import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.base.adapter.base.BaseViewAdapter
import com.lee.playandroid.base.adapter.extensions.bindAllListener
import com.lee.playandroid.base.adapter.page.submitSinglePage
import com.lee.playandroid.base.base.BaseBindingNavigationFragment
import com.lee.playandroid.base.extensions.collectState
import com.lee.playandroid.base.tools.SystemBarTools.hasSoftInputShow
import com.lee.playandroid.base.tools.SystemBarTools.hideSoftInput
import com.lee.playandroid.base.tools.SystemBarTools.parentTouchHideSoftInput
import com.lee.playandroid.common.entity.SearchHistory
import com.lee.playandroid.common.extensions.actionFailed
import com.lee.playandroid.common.ui.widget.MainLoadResource
import com.lee.playandroid.search.R
import com.lee.playandroid.search.databinding.FragmentSearchBinding
import com.lee.playandroid.search.model.entity.SearchHotUI
import com.lee.playandroid.search.ui.adapter.SearchHistoryAdapter
import com.lee.playandroid.search.ui.adapter.SearchHotAdapter
import com.lee.playandroid.search.viewmodel.SearchViewEvent
import com.lee.playandroid.search.viewmodel.SearchViewIntent
import com.lee.playandroid.search.viewmodel.SearchViewModel
import com.lee.playandroid.search.viewmodel.SearchViewState

/**
 * 搜索页面
 * @author jv.lee
 * @date 2021/11/19
 */
class SearchFragment : BaseBindingNavigationFragment<FragmentSearchBinding>() {

    private val viewModel by viewModels<SearchViewModel>()

    private var mHotAdapter: SearchHotAdapter? = null
    private var mHistoryAdapter: SearchHistoryAdapter? = null

    override fun bindView() {
        // 设置点击空白区域隐藏软键盘
        requireActivity().window.parentTouchHideSoftInput()

        // 清除历史记录点击事件
        mBinding.tvHistoryClear.setOnClickListener {
            viewModel.dispatch(SearchViewIntent.ClearHistory)
        }

        // 搜索输入框内容监听
        mBinding.editQuery.setOnEditorActionListener { textView, actionId, _ ->
            val text = textView.text
            if (actionId == IME_ACTION_SEARCH && text.isNotEmpty()) {
                viewModel.dispatch(SearchViewIntent.NavigationSearchResult(text.toString()))
            }
            return@setOnEditorActionListener false
        }

        // 搜索热词列表适配器设置
        if (mBinding.rvHotContainer.adapter == null) {
            SearchHotAdapter(requireContext()).apply {
                mHotAdapter = this
                bindRecyclerView(mBinding.rvHotContainer, loadStateEnable = false)
                setOnItemClickListener(object :
                    BaseViewAdapter.OnItemClickListener<SearchHotUI> {
                    override fun onItemClick(view: View, entity: SearchHotUI, position: Int) {
                        viewModel.dispatch(SearchViewIntent.NavigationSearchResult(entity.key))
                    }
                })
            }
        }

        // 搜索历史列表适配器设置
        if (mBinding.rvHistoryContainer.adapter == null) {
            SearchHistoryAdapter(requireContext()).apply {
                mHistoryAdapter = this
                bindRecyclerView(mBinding.rvHistoryContainer, loadStateEnable = false)
                setOnItemClickListener(object :
                    BaseViewAdapter.OnItemClickListener<SearchHistory> {
                    override fun onItemClick(view: View, entity: SearchHistory, position: Int) {
                        viewModel.dispatch(SearchViewIntent.NavigationSearchResult(entity.key))
                    }
                })
                setOnItemChildClickListener(object :
                    BaseViewAdapter.OnItemChildView<SearchHistory> {
                    override fun onItemChildClick(view: View, entity: SearchHistory, position: Int) {
                        viewModel.dispatch(SearchViewIntent.DeleteHistory(entity.key))
                    }
                }, R.id.iv_delete)
            }
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
        mBinding.rvHotContainer.adapter = null
        mBinding.rvHistoryContainer.adapter = null
        mHistoryAdapter = null
        mHistoryAdapter = null
        super.onDestroyView()
    }

    /**
     * empty布局显示隐藏
     */
    private fun viewEmptyVisible(visible: Boolean) {
        mBinding.rvHistoryContainer.visibility =
            if (visible) View.GONE else View.VISIBLE
        mBinding.tvHistoryEmpty.visibility =
            if (visible) View.VISIBLE else View.GONE
    }
}