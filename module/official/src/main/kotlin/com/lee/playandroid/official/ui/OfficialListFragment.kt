package com.lee.playandroid.official.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.base.adapter.base.BaseViewAdapter
import com.lee.playandroid.base.viewstate.UiStatePage
import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.ui.base.BaseListFragment
import com.lee.playandroid.official.ui.adapter.OfficialListAdapter
import com.lee.playandroid.official.viewmodel.OfficialListViewAction
import com.lee.playandroid.official.viewmodel.OfficialListViewModel
import com.lee.playandroid.router.navigateDetails
import kotlinx.coroutines.flow.StateFlow

/**
 * 公众号列表页
 * @author jv.lee
 * @date 2021/11/8
 */
class OfficialListFragment : BaseListFragment() {

    companion object {
        /** 文章id参数 */
        const val ARG_PARAMS_ID = "id"

        fun newInstance(id: Long) = OfficialListFragment().apply {
            arguments = Bundle().apply { putLong(ARG_PARAMS_ID, id) }
        }
    }

    private val viewModel by viewModels<OfficialListViewModel>()

    override fun createAdapter(): BaseViewAdapter<Content> {
        return OfficialListAdapter(requireContext(), arrayListOf())
    }

    override fun requestContentList(status: Int) {
        viewModel.dispatch(OfficialListViewAction.RequestPage(status))
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