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
import com.lee.playandroid.base.base.BaseBindingNavigationFragment
import com.lee.playandroid.base.extensions.arguments
import com.lee.playandroid.base.extensions.findParentFragment
import com.lee.playandroid.base.extensions.toast
import com.lee.playandroid.base.uistate.LoadStatus
import com.lee.playandroid.base.uistate.collectCallback
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
import com.lee.playandroid.todo.viewmodel.TodoListViewEvent
import com.lee.playandroid.todo.viewmodel.TodoListViewIntent
import com.lee.playandroid.todo.viewmodel.TodoListViewModel

/**
 * todo列表数据页 (待完成/已完成)
 * @author jv.lee
 * @date 2021/12/23
 */
class TodoListFragment :
    BaseBindingNavigationFragment<FragmentTodoListBinding>(),
    BaseViewAdapter.OnItemChildView<TodoData>,
    BaseViewAdapter.LoadErrorListener,
    BaseViewAdapter.AutoLoadMoreListener,
    TodoActionListener {

    companion object {
        /** 页面构建状态类型 已完成：[ARG_STATUS_UPCOMING] 待完成：[ARG_STATUS_COMPLETE] */
        const val ARG_PARAMS_STATUS = "status"

        /** 待完成TODO列表状态值 */
        const val ARG_STATUS_UPCOMING = 0

        /** 已完成TODO列表状态值 */
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

    private val viewModel by viewModels<TodoListViewModel>()

    private val slidingPaneItemTouchListener by lazy {
        SlidingPaneItemTouchListener(requireContext())
    }

    private var mAdapter: TodoListAdapter? = null

    override fun bindView() {
        mBinding.rvContainer.itemAnimator = null
        // 添加自定义侧滑菜单触摸监听器
        mBinding.rvContainer.addOnItemTouchListener(slidingPaneItemTouchListener)
        if (mBinding.rvContainer.adapter == null) {
            TodoListAdapter(requireContext(), status).apply {
                mAdapter = this
                bindRecyclerView(mBinding.rvContainer)
                bindAllListener(this@TodoListFragment)
            }
        }
        // 添加todo日期吸顶itemDecoration
        mBinding.rvContainer.addItemDecoration(StickyDateItemDecoration(requireContext(), mAdapter))
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchOnLifecycle {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    // 重置sliding状态
                    is TodoListViewEvent.ResetSlidingState -> {
                        mBinding.rvContainer.closeAllItems()
                    }
                    // 列表item状态更新失败事件
                    is TodoListViewEvent.ActionFailed -> {
                        actionFailed(event.error)
                    }
                    // 列表item更新成功事件
                    is TodoListViewEvent.UpdateTodoActionSuccess -> {
                        mAdapter?.getData()?.removeAt(event.position)
                        mAdapter?.notifyItemRemoved(event.position)
                        toast(getString(R.string.todo_move_success))
                        findParentFragment<TodoFragment>()?.moveTodoItem(event.todo)
                    }
                    // 列表item移除成功事件
                    is TodoListViewEvent.DeleteTodoActionSuccess -> {
                        mAdapter?.getData()?.removeAt(event.position)
                        mAdapter?.notifyItemRemoved(event.position)
                        toast(getString(R.string.todo_delete_success))
                    }
                    // todo列表type变更回调重新刷新页面
                    is TodoListViewEvent.ResetRequestType -> {
                        mAdapter?.initStatusView()
                        mAdapter?.pageLoading()
                        viewModel.dispatch(TodoListViewIntent.RequestPage(LoadStatus.REFRESH))
                    }
                    // 导航至todo编辑页事件
                    is TodoListViewEvent.NavigationEditTodoPage -> {
                        findNavController().navigate(
                            R.id.action_todo_fragment_to_create_todo_fragment,
                            event.bundle
                        )
                    }
                }
            }
        }

        launchOnLifecycle {
            // todo列表数据监听填充
            viewModel.todoDataFlow.collectCallback<PageData<TodoData>>(
                success = {
                    mAdapter?.submitData(it, diff = true)
                },
                error = {
                    mAdapter?.submitFailed()
                    actionFailed(it)
                }
            )
        }
    }

    override fun onItemChildClick(view: View, entity: TodoData, position: Int) {
        when (view.id) {
            R.id.const_container -> {
                // todoList item点击导航至编辑页
                viewModel.dispatch(TodoListViewIntent.NavigationEditTodoPage(entity))
            }
            R.id.btn_done -> {
                // todoList 状态更改按钮点击通知更新todo状态（完成/未完成）
                viewModel.dispatch(TodoListViewIntent.RequestUpdate(position))
            }
            R.id.btn_delete -> {
                // todoList 删除按钮点击通知移除该todoItem
                viewModel.dispatch(TodoListViewIntent.RequestDelete(position))
            }
        }
    }

    override fun autoLoadMore() {
        viewModel.dispatch(TodoListViewIntent.RequestPage(LoadStatus.LOAD_MORE))
    }

    override fun pageReload() {
        viewModel.dispatch(TodoListViewIntent.RequestPage(LoadStatus.REFRESH))
    }

    override fun itemReload() {
        viewModel.dispatch(TodoListViewIntent.RequestPage(LoadStatus.RELOAD))
    }

    override fun addAction(todo: TodoData?) {
        todo ?: return
        if (status == ARG_STATUS_UPCOMING) {
            viewModel.dispatch(TodoListViewIntent.RequestPage(LoadStatus.REFRESH))
        }
    }

    override fun updateAction(todo: TodoData?) {
        todo ?: return
        if (todo.status == status) {
            viewModel.dispatch(TodoListViewIntent.RequestPage(LoadStatus.REFRESH))
        }
    }

    override fun moveAction(todo: TodoData) {
        if (todo.status == status) {
            viewModel.dispatch(TodoListViewIntent.RequestPage(LoadStatus.REFRESH))
        }
    }

    override fun notifyAction(type: Int) {
        viewModel.dispatch(TodoListViewIntent.CheckResetRequestType(type = type))
    }

    override fun onDestroyView() {
        mBinding.rvContainer.removeOnItemTouchListener(slidingPaneItemTouchListener)
        mBinding.rvContainer.adapter = null
        mAdapter = null
        super.onDestroyView()
    }
}