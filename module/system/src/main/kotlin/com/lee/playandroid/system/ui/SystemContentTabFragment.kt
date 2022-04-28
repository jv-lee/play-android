package com.lee.playandroid.system.ui

import androidx.fragment.app.Fragment
import com.lee.library.extensions.arguments
import com.lee.library.extensions.argumentsList
import com.lee.library.viewstate.UiStateLiveData
import com.lee.library.viewstate.stateLive
import com.lee.playandroid.library.common.entity.Tab
import com.lee.playandroid.library.common.ui.BaseTabFragment

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

    override fun requestTabs() {
    }

    override fun createChildFragment(id: Long): Fragment {
        return SystemContentListFragment.newInstance(id)
    }

    override fun dataObserveState(): UiStateLiveData {
        return stateLive { tabData }
    }
}