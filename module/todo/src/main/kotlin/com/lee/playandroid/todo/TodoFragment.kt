package com.lee.playandroid.todo

import com.lee.library.adapter.core.UiPager2Adapter
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.playandroid.todo.databinding.FragmentTodoBinding

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
            arrayListOf(TodoUpcomingFragment(), TodoCompleteFragment())
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