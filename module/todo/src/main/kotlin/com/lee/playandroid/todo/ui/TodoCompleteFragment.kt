package com.lee.playandroid.todo.ui

import androidx.fragment.app.viewModels
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.mvvm.ui.observeState
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.library.common.entity.TodoData
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.FragmentTodoCompleteBinding
import com.lee.playandroid.todo.viewmodel.TodoViewModel

private const val COMPLETE_STATUS = 1

/**
 * @author jv.lee
 * @date 2021/12/23
 * @description
 */
class TodoCompleteFragment : BaseFragment(R.layout.fragment_todo_complete) {

    private val binding by binding(FragmentTodoCompleteBinding::bind)

    private val viewModel by viewModels<TodoViewModel> {
        TodoViewModel.CreateFactory(COMPLETE_STATUS)
    }

    override fun bindView() {

    }

    override fun bindData() {
        viewModel.todoDataLive.observeState<PageData<TodoData>>(this, success = {

        }, error = {

        })
    }
}