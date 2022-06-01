package com.lee.playandroid.project.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lee.playandroid.common.ui.base.BaseTabFragment
import com.lee.playandroid.common.ui.base.BaseTabViewAction
import com.lee.playandroid.common.ui.base.BaseTabViewEvent
import com.lee.playandroid.common.ui.base.BaseTabViewState
import com.lee.playandroid.project.R
import com.lee.playandroid.project.viewmodel.ProjectViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * 项目Tab页面
 * @author jv.lee
 * @date 2021/11/2
 */
class ProjectFragment : BaseTabFragment() {

    private val viewModel by viewModels<ProjectViewModel>()

    override fun bindView() {
        super.bindView()
        findBinding().toolbar.setTitleText(getString(R.string.project_title))
    }


    override fun createChildFragment(id: Long): Fragment {
        return ProjectListFragment.newInstance(id)
    }

    override fun requestData() {
        viewModel.dispatch(BaseTabViewAction.RequestData)
    }

    override fun viewEvents(): Flow<BaseTabViewEvent> {
        return viewModel.viewEvents
    }

    override fun viewStates(): StateFlow<BaseTabViewState> {
        return viewModel.viewStates
    }

}