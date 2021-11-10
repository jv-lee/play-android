package com.lee.playandroid.project.ui

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.extensions.viewModelByFactory
import com.lee.library.mvvm.ui.UiState
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.ui.BaseListFragment
import com.lee.playandroid.project.ui.adapter.ProjectListAdapter
import com.lee.playandroid.project.viewmodel.ProjectListViewModel
import com.lee.playandroid.router.navigateDetails

/**
 * @author jv.lee
 * @data 2021/11/9
 * @description
 */
class ProjectListFragment : BaseListFragment() {

    companion object {
        const val ARG_PARAMS_ID = "arg_params_id"

        fun newInstance(id: Long) = ProjectListFragment().apply {
            arguments = Bundle().apply { putLong(ARG_PARAMS_ID, id) }
        }
    }

    private val viewModel by viewModelByFactory<ProjectListViewModel>()

    private val mAdapter by lazy { ProjectListAdapter(requireContext(), arrayListOf()) }

    override fun findAdapter(): BaseViewAdapter<Content> {
        return mAdapter
    }

    override fun requestContentList(status: Int) {
        viewModel.requestContentList(status)
    }

    override fun navigationDetails(link: String) {
        findNavController().navigateDetails(link)
    }

    override fun dataObserveState(): LiveData<UiState> {
        return viewModel.contentListLive
    }

}