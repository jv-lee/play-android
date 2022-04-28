package com.lee.playandroid.project.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lee.library.viewstate.UiStateLiveData
import com.lee.playandroid.library.common.ui.BaseTabFragment
import com.lee.playandroid.project.R
import com.lee.playandroid.project.viewmodel.ProjectViewModel

/**
 * @author jv.lee
 * @date 2021/11/2
 * @description 项目Tab页面
 */
class ProjectFragment : BaseTabFragment() {

    private val viewModel by viewModels<ProjectViewModel>()

    override fun bindView() {
        super.bindView()
        findBinding().toolbar.setTitleText(getString(R.string.project_title))
    }

    override fun requestTabs() {
        viewModel.requestTabs()
    }

    override fun createChildFragment(id: Long): Fragment {
        return ProjectListFragment.newInstance(id)
    }

    override fun dataObserveState(): UiStateLiveData {
        return viewModel.tabsLive
    }
}