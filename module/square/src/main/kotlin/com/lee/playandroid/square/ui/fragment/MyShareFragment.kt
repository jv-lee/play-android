package com.lee.playandroid.square.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.base.adapter.base.BaseViewAdapter
import com.lee.playandroid.base.adapter.extensions.bindAllListener
import com.lee.playandroid.base.adapter.page.submitData
import com.lee.playandroid.base.adapter.page.submitFailed
import com.lee.playandroid.base.base.BaseBindingNavigationFragment
import com.lee.playandroid.base.dialog.LoadingDialog
import com.lee.playandroid.base.extensions.collectState
import com.lee.playandroid.base.extensions.dismiss
import com.lee.playandroid.base.extensions.show
import com.lee.playandroid.base.extensions.toast
import com.lee.playandroid.base.interadp.setClickListener
import com.lee.playandroid.base.uistate.LoadStatus
import com.lee.playandroid.base.uistate.collectCallback
import com.lee.playandroid.base.widget.SlidingPaneItemTouchListener
import com.lee.playandroid.base.widget.closeAllItems
import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.entity.PageData
import com.lee.playandroid.common.extensions.actionFailed
import com.lee.playandroid.common.ui.adapter.SimpleTextAdapter
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.square.R
import com.lee.playandroid.square.databinding.FragmentMyShareBinding
import com.lee.playandroid.square.viewmodel.MyShareViewEvent
import com.lee.playandroid.square.viewmodel.MyShareViewIntent
import com.lee.playandroid.square.viewmodel.MyShareViewModel
import com.lee.playandroid.square.viewmodel.MyShareViewState
import com.lee.playandroid.common.R as CR

/**
 * 我的分享页面
 * @author jv.lee
 * @date 2021/12/13
 */
class MyShareFragment :
    BaseBindingNavigationFragment<FragmentMyShareBinding>(),
    BaseViewAdapter.LoadErrorListener,
    BaseViewAdapter.AutoLoadMoreListener,
    BaseViewAdapter.OnItemChildView<Content> {

    companion object {
        /** 页面回传刷新数据 */
        const val REQUEST_KEY_REFRESH = "requestKey:refresh"
    }

    private val viewModel by viewModels<MyShareViewModel>()

    private val slidingPaneItemTouchListener by lazy {
        SlidingPaneItemTouchListener(
            requireContext()
        )
    }

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    private var mAdapter: SimpleTextAdapter? = null

    override fun bindView() {
        mBinding.toolbar.setClickListener {
            moreClick {
                findNavController().navigate(
                    R.id.action_my_share_fragment_to_create_share_fragment
                )
            }
        }

        mBinding.rvContainer.addOnItemTouchListener(slidingPaneItemTouchListener)
        if (mBinding.rvContainer.adapter == null) {
            SimpleTextAdapter(requireContext()).apply {
                mAdapter = this
                bindRecyclerView(mBinding.rvContainer)
                bindAllListener(this@MyShareFragment)
            }
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        // 监听登陆页面回调时是否已经发布成功，刷新数据列表
        setFragmentResultListener(REQUEST_KEY_REFRESH) { _: String, _: Bundle ->
            viewModel.dispatch(MyShareViewIntent.RequestPage(LoadStatus.INIT))
        }

        launchOnLifecycle {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is MyShareViewEvent.DeleteShareSuccess -> {
                        mAdapter?.getData()?.removeAt(event.position)
                        mAdapter?.notifyItemRemoved(event.position)
                        toast(getString(R.string.share_delete_success))
                    }
                    is MyShareViewEvent.DeleteShareFailed -> {
                        actionFailed(event.error)
                    }
                    is MyShareViewEvent.ResetSlidingState -> {
                        mBinding.rvContainer.closeAllItems()
                    }
                }
            }
        }

        launchOnLifecycle {
            viewModel.myShareFlow.collectCallback<PageData<Content>>(
                success = {
                    mAdapter?.submitData(it, diff = true)
                },
                error = {
                    mAdapter?.submitFailed()
                    actionFailed(it)
                }
            )
        }

        launchOnLifecycle {
            viewModel.viewStates.collectState(MyShareViewState::isLoading) {
                if (it) show(loadingDialog) else dismiss(loadingDialog)
            }
        }
    }

    override fun autoLoadMore() {
        viewModel.dispatch(MyShareViewIntent.RequestPage(LoadStatus.LOAD_MORE))
    }

    override fun pageReload() {
        viewModel.dispatch(MyShareViewIntent.RequestPage(LoadStatus.REFRESH))
    }

    override fun itemReload() {
        viewModel.dispatch(MyShareViewIntent.RequestPage(LoadStatus.RELOAD))
    }

    override fun onItemChildClick(view: View, entity: Content, position: Int) {
        when (view.id) {
            CR.id.frame_container -> {
                findNavController()
                    .navigateDetails(entity.title, entity.link, entity.id, entity.collect)
            }
            CR.id.btn_delete -> {
                viewModel.dispatch(MyShareViewIntent.DeleteShare(position))
            }
        }
    }

    override fun onDestroyView() {
        mBinding.rvContainer.removeOnItemTouchListener(slidingPaneItemTouchListener)
        mBinding.rvContainer.adapter = null
        mAdapter = null
        super.onDestroyView()
    }

}