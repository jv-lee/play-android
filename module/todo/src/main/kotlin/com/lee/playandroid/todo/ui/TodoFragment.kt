package com.lee.playandroid.todo.ui

import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.core.UiPager2Adapter
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.FragmentTodoBinding
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.COMPLETE_STATUS
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.UPCOMING_STATUS

/**
 * @author jv.lee
 * @date 2021/12/23
 * @description TODO页面
 */
class TodoFragment : BaseFragment(R.layout.fragment_todo) {

    private val binding by binding(FragmentTodoBinding::bind)

    override fun bindView() {
        binding.vpContainer.isUserInputEnabled = false
        binding.vpContainer.adapter = UiPager2Adapter(
            this,
            arrayListOf(
                TodoListFragment.newInstance(UPCOMING_STATUS),
                TodoListFragment.newInstance(COMPLETE_STATUS)
            )
        )

        binding.navigationBar.bindViewPager(binding.vpContainer)

        binding.floatingButton.setOnClickListener {
            findNavController().navigate(R.id.action_todo_fragment_to_create_todo_fragment)
        }
    }

    override fun bindData() {

    }
}