package com.lee.playandroid.me.ui.fragment

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.base.adapter.base.BaseViewAdapter
import com.lee.playandroid.base.adapter.extensions.bindAllListener
import com.lee.playandroid.base.adapter.page.submitData
import com.lee.playandroid.base.adapter.page.submitFailed
import com.lee.playandroid.base.base.BaseNavigationFragment
import com.lee.playandroid.base.dialog.LoadingDialog
import com.lee.playandroid.base.extensions.binding
import com.lee.playandroid.base.extensions.dismiss
import com.lee.playandroid.base.extensions.show
import com.lee.playandroid.base.extensions.toast
import com.lee.playandroid.base.viewstate.LoadStatus
import com.lee.playandroid.base.viewstate.collectState
import com.lee.playandroid.base.widget.SlidingPaneItemTouchListener
import com.lee.playandroid.base.widget.closeAllItems
import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.entity.PageData
import com.lee.playandroid.common.extensions.actionFailed
import com.lee.playandroid.common.ui.adapter.SimpleTextAdapter
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentCollectBinding
import com.lee.playandroid.me.viewmodel.CollectViewAction
import com.lee.playandroid.me.viewmodel.CollectViewEvent
import com.lee.playandroid.me.viewmodel.CollectViewModel
import com.lee.playandroid.me.viewmodel.CollectViewState
import com.lee.playandroid.router.navigateDetails

/**
 * 收藏列表页
 * @author jv.lee
 * @date 2021/11/25
 */
class CollectFragment :
    BaseNavigationFragment(R.layout.fragment_collect),
    BaseViewAdapter.LoadErrorListener,
    BaseViewAdapter.AutoLoadMoreListener,
    BaseViewAdapter.OnItemChildView<Content> {

    private val viewModel by viewModels<CollectViewModel>()

    private val binding by binding(FragmentCollectBinding::bind)

    private val slidingPaneItemTouchListener by lazy {
        SlidingPaneItemTouchListener(requireContext())
    }

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    private var mAdapter: SimpleTextAdapter? = null

    override fun bindView() {
        binding.rvContainer.addOnItemTouchListener(slidingPaneItemTouchListener)
        if (binding.rvContainer.adapter == null) {
            binding.rvContainer.adapter = SimpleTextAdapter(requireContext()).apply {
                mAdapter = this
                initStatusView()
                pageLoading()
                bindAllListener(this@CollectFragment)
            }.getProxy()
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchWhenResumed {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is CollectViewEvent.UnCollectSuccess -> {
                        mAdapter?.getData()?.removeAt(event.position)
                        mAdapter?.notifyItemRemoved(event.position)
                        toast(getString(R.string.collect_remove_item_success))
                    }
                    is CollectViewEvent.UnCollectFailed -> {
                        actionFailed(event.error)
                    }
                    is CollectViewEvent.ResetSlidingState -> {
                        binding.rvContainer.closeAllItems()
                    }
                }
            }
        }
        launchWhenResumed {
            viewModel.collectFlow.collectState<PageData<Content>>(
                success = {
                    mAdapter?.submitData(it, diff = true)
                },
                error = {
                    mAdapter?.submitFailed()
                    actionFailed(it)
                }
            )
        }

        launchWhenResumed {
            viewModel.viewStates.collectState(CollectViewState::isLoading) {
                if (it) show(loadingDialog) else dismiss(loadingDialog)
            }
        }
    }

    override fun autoLoadMore() {
        viewModel.dispatch(CollectViewAction.RequestPage(LoadStatus.LOAD_MORE))
    }

    override fun pageReload() {
        viewModel.dispatch(CollectViewAction.RequestPage(LoadStatus.REFRESH))
    }

    override fun itemReload() {
        viewModel.dispatch(CollectViewAction.RequestPage(LoadStatus.RELOAD))
    }

    override fun onItemChild(view: View, entity: Content, position: Int) {
        when (view.id) {
            R.id.frame_container -> {
                findNavController().navigateDetails(
                    entity.title,
                    entity.link,
                    entity.id,
                    entity.collect
                )
            }
            R.id.btn_delete -> {
                viewModel.dispatch(CollectViewAction.UnCollect(position))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvContainer.removeOnItemTouchListener(slidingPaneItemTouchListener)
        binding.rvContainer.adapter = null
        mAdapter = null
    }
}