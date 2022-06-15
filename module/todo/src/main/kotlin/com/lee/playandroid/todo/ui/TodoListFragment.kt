package com.lee.playandroid.todo.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.base.adapter.base.BaseViewAdapter
import com.lee.playandroid.base.adapter.extensions.bindAllListener
import com.lee.playandroid.base.adapter.page.submitData
import com.lee.playandroid.base.adapter.page.submitFailed
import com.lee.playandroid.base.base.BaseNavigationFragment
import com.lee.playandroid.base.extensions.arguments
import com.lee.playandroid.base.extensions.binding
import com.lee.playandroid.base.extensions.findParentFragment
import com.lee.playandroid.base.extensions.toast
import com.lee.playandroid.base.viewstate.LoadStatus
import com.lee.playandroid.base.viewstate.collectState
import com.lee.playandroid.base.widget.SlidingPaneItemTouchListener
import com.lee.playandroid.base.widget.closeAllItems
import com.lee.playandroid.common.entity.PageData
import com.lee.playandroid.common.entity.TodoData
import com.lee.playandroid.common.extensions.actionFailed
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
 * todo列表数据页 (待完成/已完成)
 * @author jv.lee
 * @date 2021/12/23
 */
class TodoListFragment : BaseNavigationFragment(R.layout.fragment_todo_list),
    BaseViewAdapter.OnItemChildView<TodoData>,
    BaseViewAdapter.LoadErrorListener,
    BaseViewAdapter.AutoLoadMoreListener,
    TodoActionListener {

    companion object {
        /**
         * 页面构建状态类型 1:已完成 0:待完成
         */
        const val ARG_PARAMS_STATUS = "status"

        /**
         * 待完成TODO列表状态值
         */
        const val ARG_STATUS_UPCOMING = 0

        /**
         * 已完成TODO列表状态值
         */
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
                        mAdapter?.data?.removeAt(event.position)
                        mAdapter?.notifyItemRemoved(event.position)
                        toast(getString(R.string.todo_move_success))
                        findParentFragment<TodoFragment>()?.moveTodoItem(event.todo)
                    }
                    is TodoListViewEvent.DeleteTodoActionSuccess -> {
                        mAdapter?.data?.removeAt(event.position)
                        mAdapter?.notifyItemRemoved(event.position)
                        toast(getString(R.string.todo_delete_success))
                    }
                    is TodoListViewEvent.ResetRequestType -> {
                        mAdapter?.initStatusView()
                        mAdapter?.pageLoading()
                        viewModel.dispatch(TodoListViewAction.RequestPage(LoadStatus.REFRESH))
                    }
                    is TodoListViewEvent.NavigationEditTodoPage -> {
                        findNavController().navigate(
                            R.id.action_todo_fragment_to_create_todo_fragment,
                            event.bundle
                        )
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
            R.id.const_container -> {
                viewModel.dispatch(TodoListViewAction.NavigationEditTodoPage(entity))
            }
            R.id.btn_done -> {
                viewModel.dispatch(TodoListViewAction.RequestUpdate(position))
            }
            R.id.btn_delete -> {
                viewModel.dispatch(TodoListViewAction.RequestDelete(position))
            }
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

}