package com.lee.playandroid.system.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.lee.library.extensions.arguments
import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.stateLive
import com.lee.playandroid.library.common.entity.Tab
import com.lee.playandroid.library.common.ui.BaseTabFragment
import com.lee.playandroid.system.constants.Constants

/**
 * @author jv.lee
 * @data 2021/11/10
 * @description
 */
class ContentTabFragment : BaseTabFragment() {

    private val title by arguments<String>(Constants.CONTENT_TAB_TITLE_KEY)
    private val tabData by arguments<ArrayList<Tab>>(Constants.CONTENT_TAB_DATA_KEY)

    override fun bindView() {
        super.bindView()
        findBinding().toolbar.setTitleText(title)
    }

    override fun requestTabs() {
    }

    override fun createChildFragment(id: Long): Fragment {
        return ContentListFragment.newInstance(id)
    }

    override fun dataObserveState(): LiveData<UiState> {
        return stateLive { tabData }
    }
}