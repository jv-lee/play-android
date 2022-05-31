package com.lee.playandroid.system.ui

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.extensions.viewModelByFactory
import com.lee.library.viewstate.UiStatePage
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.ui.base.BaseListFragment
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.system.ui.adapter.SystemContentListAdapter
import com.lee.playandroid.system.viewmodel.SystemContentListViewAction
import com.lee.playandroid.system.viewmodel.SystemContentListViewModel
import kotlinx.coroutines.flow.StateFlow

/**
 * 体系内容数据列表类
 * @see SystemContentTabFragment 子列表Fragment
 * @author jv.lee
 * @date 2021/11/10
 */
class SystemContentListFragment : BaseListFragment() {

    companion object {
        const val ARG_PARAMS_ID = "id"

        fun newInstance(id: Long) = SystemContentListFragment().apply {
            arguments = Bundle().apply { putLong(ARG_PARAMS_ID, id) }
        }
    }

    private val viewModel by viewModelByFactory<SystemContentListViewModel>()

    override fun createAdapter(): BaseViewAdapter<Content> {
        return SystemContentListAdapter(requireContext(), arrayListOf())
    }

    override fun requestContentList(status: Int) {
        viewModel.dispatch(SystemContentListViewAction.RequestPage(status))
    }

    override fun navigationDetails(content: Content) {
        findNavController().navigateDetails(
            content.title, content.link, content.id, content.collect
        )
    }

    override fun dataFlow(): StateFlow<UiStatePage> {
        return viewModel.contentListFlow
    }

}