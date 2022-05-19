package com.lee.playandroid.system.ui

import androidx.fragment.app.Fragment
import com.lee.library.extensions.arguments
import com.lee.library.extensions.argumentsList
import com.lee.playandroid.library.common.entity.Tab
import com.lee.playandroid.library.common.ui.base.BaseTabFragment
import com.lee.playandroid.library.common.ui.base.BaseTabViewEvent
import com.lee.playandroid.library.common.ui.base.BaseTabViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

/**
 * @author jv.lee
 * @date 2021/11/10
 * @description 体系内容Tab页
 * @see SystemContentFragment 体系列表跳转至当前该体系tab页面
 */
class SystemContentTabFragment : BaseTabFragment() {

    companion object {
        const val ARG_PARAMS_TAB_TITLE = "tabTitle"
        const val ARG_PARAMS_TAB_DATA = "tabData"
    }

    private val title by arguments<String>(ARG_PARAMS_TAB_TITLE)
    private val tabData by argumentsList<Tab>(ARG_PARAMS_TAB_DATA)

    override fun bindView() {
        super.bindView()
        findBinding().toolbar.setTitleText(title)
    }

    override fun createChildFragment(id: Long): Fragment {
        return SystemContentListFragment.newInstance(id)
    }

    override fun requestData() {
    }

    override fun viewEvents(): Flow<BaseTabViewEvent> {
        return flow { }
    }

    override fun viewStates(): StateFlow<BaseTabViewState> {
        return MutableStateFlow(BaseTabViewState(tabList = tabData, loading = false))
    }


}