package com.lee.playandroid.project.ui

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.extensions.viewModelByFactory
import com.lee.library.viewstate.UiStatePage
import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.ui.base.BaseListFragment
import com.lee.playandroid.project.ui.adapter.ProjectListAdapter
import com.lee.playandroid.project.viewmodel.ProjectListViewAction
import com.lee.playandroid.project.viewmodel.ProjectListViewModel
import com.lee.playandroid.router.navigateDetails
import kotlinx.coroutines.flow.StateFlow

/**
 * 项目列表页
 * @author jv.lee
 * @date 2021/11/9
 */
class ProjectListFragment : BaseListFragment() {

    companion object {
        const val ARG_PARAMS_ID = "id"

        fun newInstance(id: Long) = ProjectListFragment().apply {
            arguments = Bundle().apply { putLong(ARG_PARAMS_ID, id) }
        }
    }

    private val viewModel by viewModelByFactory<ProjectListViewModel>()

    override fun createAdapter(): BaseViewAdapter<Content> {
        return ProjectListAdapter(requireContext(), arrayListOf())
    }

    override fun requestContentList(status: Int) {
        viewModel.dispatch(ProjectListViewAction.RequestPage(status))
    }

    override fun navigationDetails(content: Content) {
        findNavController().navigateDetails(
            content.title, content.link, content.id, content.collect
        )
    }

    override fun dataFlow(): StateFlow<UiStatePage> {
        return viewModel.contentListFlow
    }

}