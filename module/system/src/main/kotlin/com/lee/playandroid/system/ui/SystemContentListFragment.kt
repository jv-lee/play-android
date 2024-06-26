package com.lee.playandroid.system.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.base.adapter.base.BaseViewAdapter
import com.lee.playandroid.base.uistate.UiStatePage
import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.ui.base.BaseListFragment
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.system.ui.adapter.SystemContentListAdapter
import com.lee.playandroid.system.viewmodel.SystemContentListViewIntent
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
        /** 文章id */
        const val ARG_PARAMS_ID = "id"

        fun newInstance(id: Long) = SystemContentListFragment().apply {
            arguments = Bundle().apply { putLong(ARG_PARAMS_ID, id) }
        }
    }

    private val viewModel by viewModels<SystemContentListViewModel>()

    override fun createAdapter(): BaseViewAdapter<Content> {
        return SystemContentListAdapter(requireContext())
    }

    override fun requestContentList(status: Int) {
        viewModel.dispatch(SystemContentListViewIntent.RequestPage(status))
    }

    override fun navigationDetails(content: Content) {
        findNavController().navigateDetails(
            content.title,
            content.link,
            content.id,
            content.collect
        )
    }

    override fun dataFlow(): StateFlow<UiStatePage> {
        return viewModel.contentListFlow
    }
}