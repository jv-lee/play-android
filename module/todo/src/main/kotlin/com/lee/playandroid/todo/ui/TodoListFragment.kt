package com.lee.playandroid.todo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lee.library.adapter.listener.LoadErrorListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.arguments
import com.lee.library.extensions.binding
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.observeState
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.entity.TodoData
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.FragmentTodoListBinding
import com.lee.playandroid.todo.ui.adapter.TodoListAdapter
import com.lee.playandroid.todo.ui.widget.TodoDividerItemDecoration
import com.lee.playandroid.todo.viewmodel.TodoViewModel

/**
 * @author jv.lee
 * @date 2021/12/23
 * @description TODO列表数据页 (待完成/已完成)
 */
class TodoListFragment : BaseFragment(R.layout.fragment_todo_list) {

    companion object {
        // 1:已完成 0:待完成
        private const val ARG_PARAMS_STATUS = "status"

        // 待完成TODO列表状态值
        const val UPCOMING_STATUS = 0
        // 已完成TODO列表状态值
        const val COMPLETE_STATUS = 1

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

    private val viewModel by viewModels<TodoViewModel> {
        TodoViewModel.CreateFactory(status)
    }

    private lateinit var mAdapter: TodoListAdapter

    override fun bindView() {
        binding.rvContainer.addItemDecoration(TodoDividerItemDecoration(requireContext()))
        binding.rvContainer.adapter = TodoListAdapter(requireContext(), arrayListOf()).apply {
            mAdapter = this

            initStatusView()
            pageLoading()

            setAutoLoadMoreListener {
                viewModel.requestTodoData(LoadStatus.LOAD_MORE)
            }

            setLoadErrorListener(object : LoadErrorListener {
                override fun pageReload() {
                    viewModel.requestTodoData(LoadStatus.REFRESH)
                }

                override fun itemReload() {
                    viewModel.requestTodoData(LoadStatus.RELOAD)
                }
            })
        }.proxy
    }

    override fun bindData() {
        viewModel.todoDataLive.observeState<PageData<TodoData>>(this, success = {
            mAdapter.submitData(it, diff = true)
        }, error = {
            mAdapter.submitFailed()
            actionFailed(it)
        })
    }
}