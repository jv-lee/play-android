package com.lee.playandroid.todo.ui

import android.os.Bundle
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.core.UiPager2Adapter
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.playandroid.library.common.entity.TodoData
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.FragmentTodoBinding
import com.lee.playandroid.todo.ui.CreateTodoFragment.Companion.ARG_PARAMS_TYPE
import com.lee.playandroid.todo.ui.CreateTodoFragment.Companion.ARG_TYPE_CREATE
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_STATUS_COMPLETE
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_STATUS_UPCOMING
import com.lee.playandroid.todo.ui.listener.TodoActionListener

/**
 * @author jv.lee
 * @date 2021/12/23
 * @description TODO页面
 */
class TodoFragment : BaseFragment(R.layout.fragment_todo) {

    companion object {
        const val REQUEST_KEY_SAVE = "requestKey:save"
        const val REQUEST_KEY_UPDATE = "requestKey:update"
        const val REQUEST_VALUE_TODO = "requestValue:todo"
    }

    private val binding by binding(FragmentTodoBinding::bind)

    override fun bindView() {
        binding.vpContainer.isUserInputEnabled = false
        binding.vpContainer.adapter = UiPager2Adapter(
            this,
            arrayListOf(
                TodoListFragment.newInstance(ARG_STATUS_UPCOMING),
                TodoListFragment.newInstance(ARG_STATUS_COMPLETE)
            )
        )

        binding.navigationBar.bindViewPager(binding.vpContainer)

        binding.floatingButton.setOnClickListener {
            startCreateTodoPage()
        }
    }

    override fun bindData() {
        setFragmentResultListener(REQUEST_KEY_SAVE) { _: String, bundle: Bundle ->
            val todoData = bundle.getParcelable<TodoData>(REQUEST_VALUE_TODO)
                ?: return@setFragmentResultListener
            childFragmentManager.fragments.forEach {
                (it as? TodoActionListener)?.addAction(todoData)
            }
        }
    }

    /**
     * 导航到创建TODO页
     */
    private fun startCreateTodoPage() {
        val bundle = Bundle().apply {
            putInt(ARG_PARAMS_TYPE, ARG_TYPE_CREATE)
        }
        findNavController().navigate(R.id.action_todo_fragment_to_create_todo_fragment, bundle)
    }

}