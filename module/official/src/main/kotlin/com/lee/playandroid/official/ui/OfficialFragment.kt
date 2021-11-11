package com.lee.playandroid.official.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.lee.library.mvvm.ui.UiState
import com.lee.playandroid.library.common.ui.BaseTabFragment
import com.lee.playandroid.official.R
import com.lee.playandroid.official.viewmodel.OfficialViewModel

/**
 * @author jv.lee
 * @data 2021/11/2
 * @description 公众号Tab页
 */
class OfficialFragment : BaseTabFragment() {

    private val viewModel by viewModels<OfficialViewModel>()

    override fun bindView() {
        super.bindView()
        findBinding().toolbar.setTitleText(getString(R.string.official_title))
    }

    override fun requestTabs() {
        viewModel.requestTabs()
    }

    override fun createChildFragment(id: Long): Fragment {
        return OfficialListFragment.newInstance(id)
    }

    override fun dataObserveState(): LiveData<UiState> {
        return viewModel.tabsLive
    }

}