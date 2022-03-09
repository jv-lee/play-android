package com.lee.playandroid.system.ui

import androidx.fragment.app.Fragment
import com.lee.library.extensions.arguments
import com.lee.library.extensions.argumentsList
import com.lee.library.mvvm.ui.UiStateLiveData
import com.lee.library.mvvm.ui.stateLive
import com.lee.playandroid.library.common.entity.Tab
import com.lee.playandroid.library.common.ui.BaseTabFragment
import com.lee.playandroid.system.constants.Constants

/**
 * @author jv.lee
 * @date 2021/11/10
 * @description 体系内容Tab页
 * @see SystemContentFragment 体系列表跳转至当前该体系tab页面
 */
class SystemContentTabFragment : BaseTabFragment() {

    private val title by arguments<String>(Constants.ARG_PARAMS_CONTENT_TAB_TITLE)
    private val tabData by argumentsList<Tab>(Constants.ARG_PARAMS_CONTENT_TAB_DATA)

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