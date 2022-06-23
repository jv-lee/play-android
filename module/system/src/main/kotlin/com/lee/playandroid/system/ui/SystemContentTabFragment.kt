package com.lee.playandroid.system.ui

import androidx.fragment.app.Fragment
import com.lee.playandroid.base.extensions.arguments
import com.lee.playandroid.base.extensions.argumentsList
import com.lee.playandroid.common.entity.Tab
import com.lee.playandroid.common.ui.base.BaseTabFragment
import com.lee.playandroid.common.ui.base.BaseTabViewEvent
import com.lee.playandroid.common.ui.base.BaseTabViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

/**
 * 体系内容Tab页
 * @see SystemContentFragment 体系列表跳转至当前该体系tab页面
 * @author jv.lee
 * @date 2021/11/10
 */
class SystemContentTabFragment : BaseTabFragment() {

    companion object {
        /** 体系内容页标题 */
        const val ARG_PARAMS_TAB_TITLE = "tabTitle"
        /** 体系内容页tab数据 */
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