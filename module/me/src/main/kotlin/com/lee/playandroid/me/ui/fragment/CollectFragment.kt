package com.lee.playandroid.me.ui.fragment

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.adapter.listener.LoadErrorListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.toast
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.observeState
import com.lee.library.utils.NetworkUtil
import com.lee.library.widget.SwipeItemLayout
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.library.common.ui.adapter.SimpleTextAdapter
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentCollectBinding
import com.lee.playandroid.me.viewmodel.CollectViewModel
import com.lee.playandroid.router.navigateDetails
import java.util.*

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description 收藏列表页
 */
class CollectFragment : BaseFragment(R.layout.fragment_collect),
    BaseViewAdapter.OnItemChildView<Content> {

    private val viewModel by viewModels<CollectViewModel>()

    private val binding by binding(FragmentCollectBinding::bind)

    private lateinit var mAdapter: SimpleTextAdapter

    override fun bindView() {
        binding.rvContainer.addOnItemTouchListener(
            SwipeItemLayout.OnSwipeItemTouchListener(
                requireContext()
            )
        )
        binding.rvContainer.adapter = SimpleTextAdapter(requireContext(), arrayListOf()).apply {
            mAdapter = this
            initStatusView()
            pageLoading()

            setAutoLoadMoreListener {
                viewModel.requestCollect(LoadStatus.LOAD_MORE)
            }

            setLoadErrorListener(object : LoadErrorListener {
                override fun pageReload() {
                    viewModel.requestCollect(LoadStatus.REFRESH)
                }

                override fun itemReload() {
                    viewModel.requestCollect(LoadStatus.RELOAD)
                }
            })

            setOnItemChildClickListener(this@CollectFragment)
        }.proxy
    }

    override fun bindData() {
        viewModel.collectLive.observeState<PageData<Content>>(this, success = {
            mAdapter.submitData(it, diff = true)
        }, error = {
            mAdapter.submitFailed()
            actionFailed(it)
        })

        viewModel.unCollectLive.observeState<Int>(this, success = {
            toast(getString(R.string.collect_remove_item_success))
        }, error = {
            SwipeItemLayout.closeAllItems(binding.rvContainer)
            actionFailed(it)
        })
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

    private fun unCollectAction(position: Int) {
        if (NetworkUtil.isNetworkConnected(requireContext())) {
            mAdapter.data.removeAt(position)
            mAdapter.notifyItemRemoved(position)
            viewModel.requestUnCollect(position)
        } else {
            SwipeItemLayout.closeAllItems(binding.rvContainer)
            toast(getString(R.string.network_not_access))
        }

    }

}