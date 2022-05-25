package com.lee.playandroid.todo.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.base.BaseViewAdapter
import com.lee.library.adapter.extensions.bindAllListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.arguments
import com.lee.library.extensions.binding
import com.lee.library.extensions.findParentFragment
import com.lee.library.extensions.toast
import com.lee.library.utils.NetworkUtil
import com.lee.library.viewstate.LoadStatus
import com.lee.library.viewstate.collectState
import com.lee.library.widget.SlidingPaneItemTouchListener
import com.lee.library.widget.closeAllItems
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.entity.TodoData
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.FragmentTodoListBinding
import com.lee.playandroid.todo.ui.adapter.TodoListAdapter
import com.lee.playandroid.todo.ui.listener.TodoActionListener
import com.lee.playandroid.todo.ui.widget.StickyDateItemDecoration
import com.lee.playandroid.todo.viewmodel.TodoListViewAction
import com.lee.playandroid.todo.viewmodel.TodoListViewEvent
import com.lee.playandroid.todo.viewmodel.TodoListViewModel
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/12/23
 * @description TODO列表数据页 (待完成/已完成)
 */
class TodoListFragment : BaseNavigationFragment(R.layout.fragment_todo_list),
    BaseViewAdapter.OnItemChildView<TodoData>,
    BaseViewAdapter.LoadErrorListener,
    BaseViewAdapter.AutoLoadMoreListener,
    TodoActionListener {

    companion object {
        // 1:已完成 0:待完成
        const val ARG_PARAMS_STATUS = "status"

        // 待完成TODO列表状态值
        const val ARG_STATUS_UPCOMING = 0

        // 已完成TODO列表状态值
        const val ARG_STATUS_COMPLETE = 1

        fun newInstance(status: Int): Fragment {
            return TodoListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAMS_STATUS, status)
                }
            }
        }
    }

    private val status by arguments<Int>(ARG_PARAMS_STATUS)

    private val binding by binding(FragmentTodoListBinding::bind)

    private val viewModel by viewModels<TodoListViewModel>()

    private val slidingPaneItemTouchListener by lazy { SlidingPaneItemTouchListener(requireContext()) }

    private var mAdapter: TodoListAdapter? = null

    override fun bindView() {
        binding.rvContainer.itemAnimator = null
        binding.rvContainer.addOnItemTouchListener(slidingPaneItemTouchListener)
        if (binding.rvContainer.adapter == null) {
            binding.rvContainer.adapter =
                TodoListAdapter(requireContext(), status, arrayListOf()).apply {
                    mAdapter = this
                    initStatusView()
                    pageLoading()
                    bindAllListener(this@TodoListFragment)
                }.proxy
        }
        binding.rvContainer.addItemDecoration(StickyDateItemDecoration(requireContext(), mAdapter))
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchWhenResumed {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is TodoListViewEvent.ActionFailed -> {
                        binding.rvContainer.closeAllItems()
                        actionFailed(event.error)
                    }
                    is TodoListViewEvent.UpdateTodoActionSuccess -> {
                        toast(getString(R.string.todo_move_success))
                        findParentFragment<TodoFragment>()?.moveTodoItem(event.todo)
                    }
                    is TodoListViewEvent.DeleteTodoActionSuccess -> {
                        toast(getString(R.string.todo_delete_success))
                    }
                    is TodoListViewEvent.ResetRequestType -> {
                        mAdapter?.initStatusView()
                        mAdapter?.pageLoading()
                        viewModel.dispatch(TodoListViewAction.RequestPage(LoadStatus.REFRESH))
                    }
                }
            }
        }

        launchWhenResumed {
            viewModel.todoDataFlow.collectState<PageData<TodoData>>(success = {
                mAdapter?.submitData(it, diff = true)
            }, error = {
                mAdapter?.submitFailed()
                actionFailed(it)
            })
        }
    }

    override fun onItemChild(view: View, entity: TodoData, position: Int) {
        when (view.id) {
            R.id.const_container -> startEditTodoPage(entity)
            R.id.btn_done -> moveTodoAction(position)
            R.id.btn_delete -> deleteTodoAction(position)
        }
    }

    override fun autoLoadMore() {
        viewModel.dispatch(TodoListViewAction.RequestPage(LoadStatus.LOAD_MORE))
    }

    override fun pageReload() {
        viewModel.dispatch(TodoListViewAction.RequestPage(LoadStatus.REFRESH))
    }

    override fun itemReload() {
        viewModel.dispatch(TodoListViewAction.RequestPage(LoadStatus.RELOAD))
    }

    override fun addAction(todo: TodoData?) {
        todo ?: return
        if (status == ARG_STATUS_UPCOMING) {
            viewModel.dispatch(TodoListViewAction.RequestPage(LoadStatus.REFRESH))
        }
    }

    override fun updateAction(todo: TodoData?) {
        todo ?: return
        if (todo.status == status) {
            viewModel.dispatch(TodoListViewAction.RequestPage(LoadStatus.REFRESH))
        }
    }

    override fun moveAction(todo: TodoData) {
        if (todo.status == status) {
            viewModel.dispatch(TodoListViewAction.RequestPage(LoadStatus.REFRESH))
        }
    }

    override fun notifyAction(type: Int) {
        viewModel.dispatch(TodoListViewAction.CheckResetRequestType(type = type))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvContainer.removeOnItemTouchListener(slidingPaneItemTouchListener)
        binding.rvContainer.adapter = null
        mAdapter = null
    }

    /**
     * 移动TODO状态
     */
    private fun moveTodoAction(position: Int) {
        if (NetworkUtil.isNetworkConnected(requireContext())) {
            mAdapter?.data?.removeAt(position)
            mAdapter?.notifyItemRemoved(position)
            viewModel.dispatch(TodoListViewAction.RequestUpdate(position))
        } else {
            binding.rvContainer.closeAllItems()
            toast(getString(R.string.network_not_access))
        }
    }

    /**
     * 处理todo删除
     */
    private fun deleteTodoAction(position: Int) {
        if (NetworkUtil.isNetworkConnected(requireContext())) {
            mAdapter?.data?.removeAt(position)
            mAdapter?.notifyItemRemoved(position)
            viewModel.dispatch(TodoListViewAction.RequestDelete(position))
        } else {
            binding.rvContainer.closeAllItems()
            toast(getString(R.string.network_not_access))
        }
    }

    /**
     * 导航至todo编辑页
     */
    private fun startEditTodoPage(todo: TodoData) {
        val bundle = Bundle().apply {
            putInt(CreateTodoFragment.ARG_PARAMS_TYPE, CreateTodoFragment.ARG_TYPE_EDIT)
            putParcelable(CreateTodoFragment.ARG_PARAMS_TODO, todo)
        }
        findNavController().navigate(
            R.id.action_todo_fragment_to_create_todo_fragment,
            bundle
        )
    }
}