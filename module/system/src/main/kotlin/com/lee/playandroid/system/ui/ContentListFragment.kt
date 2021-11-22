package com.lee.playandroid.system.ui

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.extensions.viewModelByFactory
import com.lee.library.mvvm.ui.UiState
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.ui.BaseListFragment
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.system.ui.adapter.ContentListAdapter
import com.lee.playandroid.system.viewmodel.ContentListViewModel

/**
 * @author jv.lee
 * @data 2021/11/10
 * @description 数据列表类
 * @see ContentTabFragment 子列表Fragment
 */
class ContentListFragment : BaseListFragment() {

    companion object {
        const val ARG_PARAMS_ID = "arg_params_id"

        fun newInstance(id: Long) = ContentListFragment().apply {
            arguments = Bundle().apply { putLong(ARG_PARAMS_ID, id) }
        }
    }

    private val viewModel by viewModelByFactory<ContentListViewModel>()

    override fun createAdapter(): BaseViewAdapter<Content> {
        return ContentListAdapter(requireContext(), arrayListOf())
    }

    override fun requestContentList(status: Int) {
        viewModel.requestContentList(status)
    }

    override fun navigationDetails(title: String, link: String) {
        findNavController().navigateDetails(title, link)
    }

    override fun dataObserveState(): LiveData<UiState> {
        return viewModel.contentListLive
    }
}