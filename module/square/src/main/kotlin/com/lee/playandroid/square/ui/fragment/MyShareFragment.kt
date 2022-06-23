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
import com.lee.playandroid.base.base.BaseNavigationFragment
import com.lee.playandroid.base.extensions.binding
import com.lee.playandroid.base.extensions.toast
import com.lee.playandroid.base.interadp.setClickListener
import com.lee.playandroid.base.viewstate.LoadStatus
import com.lee.playandroid.base.viewstate.collectState
import com.lee.playandroid.base.widget.SlidingPaneItemTouchListener
import com.lee.playandroid.base.widget.closeAllItems
import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.entity.PageData
import com.lee.playandroid.common.extensions.actionFailed
import com.lee.playandroid.common.ui.adapter.SimpleTextAdapter
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.square.R
import com.lee.playandroid.square.databinding.FragmentMyShareBinding
import com.lee.playandroid.square.viewmodel.MyShareViewAction
import com.lee.playandroid.square.viewmodel.MyShareViewEvent
import com.lee.playandroid.square.viewmodel.MyShareViewModel
import kotlinx.coroutines.flow.collect
import com.lee.playandroid.common.R as CR

/**
 * 我的分享页面
 * @author jv.lee
 * @date 2021/12/13
 */
class MyShareFragment : BaseNavigationFragment(R.layout.fragment_my_share),
    BaseViewAdapter.LoadErrorListener,
    BaseViewAdapter.AutoLoadMoreListener,
    BaseViewAdapter.OnItemChildView<Content> {

    companion object {
        /** 页面回传刷新数据 */
        const val REQUEST_KEY_REFRESH = "requestKey:refresh"
    }

    private val viewModel by viewModels<MyShareViewModel>()

    private val binding by binding(FragmentMyShareBinding::bind)

    private val slidingPaneItemTouchListener by lazy { SlidingPaneItemTouchListener(requireContext()) }

    private var mAdapter: SimpleTextAdapter? = null

    override fun bindView() {
        binding.toolbar.setClickListener {
            moreClick { findNavController().navigate(R.id.action_my_share_fragment_to_create_share_fragment) }
        }

        binding.rvContainer.addOnItemTouchListener(slidingPaneItemTouchListener)
        if (binding.rvContainer.adapter == null) {
            binding.rvContainer.adapter = SimpleTextAdapter(requireContext(), arrayListOf()).apply {
                mAdapter = this
                initStatusView()
                pageLoading()
                bindAllListener(this@MyShareFragment)
            }.proxy
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        // 监听登陆页面回调时是否已经发布成功，刷新数据列表
        setFragmentResultListener(REQUEST_KEY_REFRESH) { _: String, _: Bundle ->
            viewModel.dispatch(MyShareViewAction.RequestPage(LoadStatus.INIT))
        }

        launchWhenResumed {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is MyShareViewEvent.DeleteShareSuccess -> {
                        mAdapter?.data?.removeAt(event.position)
                        mAdapter?.notifyItemRemoved(event.position)
                        toast(getString(R.string.share_delete_success))
                    }
                    is MyShareViewEvent.DeleteShareFailed -> {
                        actionFailed(event.error)
                        binding.rvContainer.closeAllItems()
                    }
                }
            }
        }

        launchWhenResumed {
            viewModel.myShareFlow.collectState<PageData<Content>>(success = {
                mAdapter?.submitData(it, diff = true)
            }, error = {
                mAdapter?.submitFailed()
                actionFailed(it)
            })
        }
    }

    override fun autoLoadMore() {
        viewModel.dispatch(MyShareViewAction.RequestPage(LoadStatus.LOAD_MORE))
    }

    override fun pageReload() {
        viewModel.dispatch(MyShareViewAction.RequestPage(LoadStatus.REFRESH))
    }

    override fun itemReload() {
        viewModel.dispatch(MyShareViewAction.RequestPage(LoadStatus.RELOAD))
    }

    override fun onItemChild(view: View, entity: Content, position: Int) {
        when (view.id) {
            CR.id.frame_container -> {
                findNavController()
                    .navigateDetails(entity.title, entity.link, entity.id, entity.collect)
            }
            CR.id.btn_delete -> {
                viewModel.dispatch(MyShareViewAction.DeleteShare(position))
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