package com.lee.playandroid.official.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lee.playandroid.library.common.ui.base.BaseTabFragment
import com.lee.playandroid.library.common.ui.base.BaseTabViewAction
import com.lee.playandroid.library.common.ui.base.BaseTabViewEvent
import com.lee.playandroid.library.common.ui.base.BaseTabViewState
import com.lee.playandroid.official.R
import com.lee.playandroid.official.viewmodel.OfficialViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * @author jv.lee
 * @date 2021/11/2
 * @description 公众号Tab页
 */
class OfficialFragment : BaseTabFragment() {

    private val viewModel by viewModels<OfficialViewModel>()

    override fun bindView() {
        super.bindView()
        findBinding().toolbar.setTitleText(getString(R.string.official_title))
    }

    override fun createChildFragment(id: Long): Fragment {
        return OfficialListFragment.newInstance(id)
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