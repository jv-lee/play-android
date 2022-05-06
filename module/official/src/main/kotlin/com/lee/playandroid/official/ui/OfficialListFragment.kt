package com.lee.playandroid.official.ui

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.extensions.viewModelByFactory
import com.lee.library.viewstate.UiStatePage
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.ui.base.BaseListFragment
import com.lee.playandroid.official.ui.adapter.OfficialListAdapter
import com.lee.playandroid.official.viewmodel.OfficialListViewAction
import com.lee.playandroid.official.viewmodel.OfficialListViewModel
import com.lee.playandroid.router.navigateDetails
import kotlinx.coroutines.flow.StateFlow

/**
 * @author jv.lee
 * @date 2021/11/8
 * @description 公众号列表页
 */
class OfficialListFragment : BaseListFragment() {

    companion object {
        const val ARG_PARAMS_ID = "id"

        fun newInstance(id: Long) = OfficialListFragment().apply {
            arguments = Bundle().apply { putLong(ARG_PARAMS_ID, id) }
        }
    }

    private val viewModel by viewModelByFactory<OfficialListViewModel>()

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