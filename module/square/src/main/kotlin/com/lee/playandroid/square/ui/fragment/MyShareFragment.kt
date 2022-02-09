package com.lee.playandroid.square.ui.fragment

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.adapter.extensions.bindAllListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.toast
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.observeState
import com.lee.library.utils.NetworkUtil
import com.lee.library.widget.SwipeItemLayout
import com.lee.library.widget.toolbar.TitleToolbar
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.library.common.ui.adapter.SimpleTextAdapter
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.square.R
import com.lee.playandroid.square.databinding.FragmentMyShareBinding
import com.lee.playandroid.square.viewmodel.MyShareViewModel
import com.lee.playandroid.library.common.R as CR

/**
 * @author jv.lee
 * @date 2021/12/13
 * @description 我的分享页面
 */
class MyShareFragment : BaseNavigationFragment(R.layout.fragment_my_share),
    BaseViewAdapter.LoadErrorListener,
    BaseViewAdapter.AutoLoadMoreListener,
    BaseViewAdapter.OnItemChildView<Content> {

    private val viewModel by viewModels<MyShareViewModel>()

    private val binding by binding(FragmentMyShareBinding::bind)

    private var mAdapter: SimpleTextAdapter? = null

    override fun bindView() {
        binding.toolbar.setClickListener(object : TitleToolbar.ClickListener() {
            override fun moreClick() {
                findNavController().navigate(R.id.action_my_share_fragment_to_create_share_fragment)
            }
        })

        binding.rvContainer.addOnItemTouchListener(
            SwipeItemLayout.OnSwipeItemTouchListener(
                requireContext()
            )
        )

        if (binding.rvContainer.adapter == null) {
            binding.rvContainer.adapter = SimpleTextAdapter(requireContext(), arrayListOf()).apply {
                mAdapter = this
                initStatusView()
                pageLoading()
                bindAllListener(this@MyShareFragment)
            }.proxy
        }
    }

    override fun bindData() {
        viewModel.myShareLive.observeState<PageData<Content>>(viewLifecycleOwner, success = {
            mAdapter?.submitData(it, diff = true)
        }, error = {
            mAdapter?.submitFailed()
            actionFailed(it)
        })

        viewModel.deleteShareLive.observeState<Int>(viewLifecycleOwner, success = { position ->
            toast(getString(R.string.share_delete_success))
        }, error = {
            SwipeItemLayout.closeAllItems(binding.rvContainer)
            actionFailed(it)
        })
    }

    override fun autoLoadMore() {
        viewModel.requestMyShareData(LoadStatus.LOAD_MORE)
    }

    override fun pageReload() {
        viewModel.requestMyShareData(LoadStatus.REFRESH)
    }

    override fun itemReload() {
        viewModel.requestMyShareData(LoadStatus.RELOAD)
    }

    override fun onItemChild(view: View, entity: Content, position: Int) {
        when (view.id) {
            CR.id.frame_container -> {
                findNavController()
                    .navigateDetails(entity.title, entity.link, entity.id, entity.collect)
            }
            CR.id.btn_delete -> {
                deleteShareAction(position)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvContainer.adapter = null
        mAdapter = null
    }

    private fun deleteShareAction(position: Int) {
        if (NetworkUtil.isNetworkConnected(requireContext())) {
            mAdapter?.data?.removeAt(position)
            mAdapter?.notifyItemRemoved(position)
            viewModel.requestDeleteShare(position)
        } else {
            SwipeItemLayout.closeAllItems(binding.rvContainer)
            toast(getString(R.string.network_not_access))
        }

    }

}