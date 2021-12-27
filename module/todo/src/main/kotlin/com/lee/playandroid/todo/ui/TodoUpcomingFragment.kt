package com.lee.playandroid.todo.ui

import androidx.fragment.app.viewModels
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.mvvm.ui.observeState
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.entity.TodoData
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.FragmentTodoUpcomingBinding
import com.lee.playandroid.todo.viewmodel.TodoViewModel

private const val UPCOMING_STATUS = 0

/**
 * @author jv.lee
 * @date 2021/12/23
 * @description
 */
class TodoUpcomingFragment : BaseFragment(R.layout.fragment_todo_upcoming) {

    private val binding by binding(FragmentTodoUpcomingBinding::bind)

    private val viewModel by viewModels<TodoViewModel> {
        TodoViewModel.CreateFactory(UPCOMING_STATUS)
    }

    override fun bindView() {

    }

    override fun bindData() {
        viewModel.todoDataLive.observeState<PageData<TodoData>>(this, success = {

        }, error = {

        })
    }
}
