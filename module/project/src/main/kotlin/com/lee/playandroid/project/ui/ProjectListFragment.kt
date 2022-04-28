package com.lee.playandroid.project.ui

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.extensions.viewModelByFactory
import com.lee.library.viewstate.UiStatePage
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.ui.BaseListFragment
import com.lee.playandroid.project.ui.adapter.ProjectListAdapter
import com.lee.playandroid.project.viewmodel.ProjectListViewModel
import com.lee.playandroid.router.navigateDetails

/**
 * @author jv.lee
 * @date 2021/11/9
 * @description 项目列表页
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
        viewModel.requestContentList(status)
    }

    override fun navigationDetails(content: Content) {
        findNavController().navigateDetails(
            content.title, content.link, content.id, content.collect
        )
    }

    override fun dataObserveState(): LiveData<_root_ide_package_.com.lee.library.viewstate.UiStatePage> {
        return viewModel.contentListLive
    }

}