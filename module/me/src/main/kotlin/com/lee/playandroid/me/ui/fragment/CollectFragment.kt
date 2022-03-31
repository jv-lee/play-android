package com.lee.playandroid.me.ui.fragment

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
import com.lee.library.mvvm.annotation.LoadStatus
import com.lee.library.mvvm.ui.stateObserve
import com.lee.library.utils.NetworkUtil
import com.lee.library.widget.SlidingPaneItemTouchListener
import com.lee.library.widget.closeAllItems
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.library.common.ui.adapter.SimpleTextAdapter
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentCollectBinding
import com.lee.playandroid.me.viewmodel.CollectViewModel
import com.lee.playandroid.router.navigateDetails

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description 收藏列表页
 */
class CollectFragment : BaseNavigationFragment(R.layout.fragment_collect),
    BaseViewAdapter.LoadErrorListener,
    BaseViewAdapter.AutoLoadMoreListener,
    BaseViewAdapter.OnItemChildView<Content> {

    private val viewModel by viewModels<CollectViewModel>()

    private val binding by binding(FragmentCollectBinding::bind)

    private var mAdapter: SimpleTextAdapter? = null

    override fun bindView() {
        binding.rvContainer.addOnItemTouchListener(SlidingPaneItemTouchListener(requireContext()))
        if (binding.rvContainer.adapter == null) {
            binding.rvContainer.adapter = SimpleTextAdapter(requireContext(), arrayListOf()).apply {
                mAdapter = this
                initStatusView()
                pageLoading()
                bindAllListener(this@CollectFragment)
            }.proxy
        }
    }

    override fun bindData() {
        viewModel.collectLive.stateObserve<PageData<Content>>(viewLifecycleOwner, success = {
            mAdapter?.submitData(it, diff = true)
        }, error = {
            mAdapter?.submitFailed()
            actionFailed(it)
        })

        viewModel.unCollectLive.stateObserve<Int>(this, success = {
            toast(getString(R.string.collect_remove_item_success))
        }, error = {
            binding.rvContainer.closeAllItems()
            actionFailed(it)
        })
    }

    override fun autoLoadMore() {
        viewModel.requestCollect(LoadStatus.LOAD_MORE)
    }

    override fun pageReload() {
        viewModel.requestCollect(LoadStatus.REFRESH)
    }

    override fun itemReload() {
        viewModel.requestCollect(LoadStatus.RELOAD)
    }

    override fun onItemChild(view: View, entity: Content, position: Int) {
        when (view.id) {
            R.id.frame_container -> {
                findNavController()
                    .navigateDetails(entity.title, entity.link, entity.id, entity.collect)
            }
            R.id.btn_delete -> {
                unCollectAction(position)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvContainer.adapter = null
        mAdapter = null
    }

    private fun unCollectAction(position: Int) {
        if (NetworkUtil.isNetworkConnected(requireContext())) {
            mAdapter?.data?.removeAt(position)
            mAdapter?.notifyItemRemoved(position)
            viewModel.requestUnCollect(position)
        } else {
            binding.rvContainer.closeAllItems()
            toast(getString(R.string.network_not_access))
        }

    }

}