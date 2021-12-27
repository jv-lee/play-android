package com.lee.playandroid.todo.ui

import com.lee.library.adapter.core.UiPager2Adapter
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.FragmentTodoBinding

private const val UPCOMING_STATUS = 0
private const val COMPLETE_STATUS = 1

/**
 * @author jv.lee
 * @date 2021/12/23
 * @description TODO页面
 */
class TodoFragment : BaseFragment(R.layout.fragment_todo) {

    private val binding by binding(FragmentTodoBinding::bind)

    private val vpAdapter by lazy {
        UiPager2Adapter(
            this,
            arrayListOf(
                TodoListFragment.newInstance(UPCOMING_STATUS),
                TodoListFragment.newInstance(COMPLETE_STATUS)
            )
        )
    }

    override fun bindView() {
        binding.vpContainer.adapter = vpAdapter
        binding.vpContainer.isUserInputEnabled = false

        binding.navigationBar.bindViewPager(binding.vpContainer)
    }

    override fun bindData() {

    }
}