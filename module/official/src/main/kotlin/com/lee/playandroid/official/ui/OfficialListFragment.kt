package com.lee.playandroid.official.ui

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.extensions.viewModelByFactory
import com.lee.library.mvvm.ui.UiState
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.ui.BaseListFragment
import com.lee.playandroid.official.ui.adapter.OfficialListAdapter
import com.lee.playandroid.official.viewmodel.OfficialListViewModel
import com.lee.playandroid.router.navigateDetails

/**
 * @author jv.lee
 * @data 2021/11/8
 * @description 公众号列表页
 */
class OfficialListFragment : BaseListFragment() {

    companion object {
        const val ARG_PARAMS_ID = "arg_params_id"

        fun newInstance(id: Long) = OfficialListFragment().apply {
            arguments = Bundle().apply { putLong(ARG_PARAMS_ID, id) }
        }
    }

    private val viewModel by viewModelByFactory<OfficialListViewModel>()

    override fun createAdapter(): BaseViewAdapter<Content> {
        return OfficialListAdapter(requireContext(), arrayListOf())
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