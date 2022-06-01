package com.lee.playandroid.todo.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.base.adapter.core.UiPagerAdapter2
import com.lee.playandroid.base.base.BaseNavigationFragment
import com.lee.playandroid.base.extensions.binding
import com.lee.playandroid.base.interadp.setClickListener
import com.lee.playandroid.base.viewstate.collectState
import com.lee.playandroid.common.entity.TodoData
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.FragmentTodoBinding
import com.lee.playandroid.todo.ui.CreateTodoFragment.Companion.ARG_PARAMS_TYPE
import com.lee.playandroid.todo.ui.CreateTodoFragment.Companion.ARG_TYPE_CREATE
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_STATUS_COMPLETE
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_STATUS_UPCOMING
import com.lee.playandroid.todo.ui.listener.TodoActionListener
import com.lee.playandroid.todo.viewmodel.TodoViewAction
import com.lee.playandroid.todo.viewmodel.TodoViewModel
import com.lee.playandroid.todo.viewmodel.TodoViewState

/**
 * TODO页面
 * @author jv.lee
 * @date 2021/12/23
 */
class TodoFragment : BaseNavigationFragment(R.layout.fragment_todo) {

    companion object {
        const val REQUEST_KEY_SAVE = "requestKey:save"
        const val REQUEST_KEY_UPDATE = "requestKey:update"
        const val REQUEST_KEY_TYPE = "requestKey:type"

        const val REQUEST_VALUE_TODO = "requestValue:todo"
        const val REQUEST_VALUE_TYPE = "requestValue:type"
    }

    private val viewModel by viewModels<TodoViewModel>()

    private val binding by binding(FragmentTodoBinding::bind)

    private var mAdapter: UiPagerAdapter2? = null

    override fun bindView() {
        binding.toolbar.setClickListener {
            // 导航至todo类型选择弹窗
            moreClick { findNavController().navigate(R.id.action_todo_fragment_to_select_todo_type_dialog) }
        }
        binding.floatingButton.setOnClickListener {
            // 导航至todo编辑页面
            findNavController().navigate(R.id.action_todo_fragment_to_create_todo_fragment,
                Bundle().apply { putInt(ARG_PARAMS_TYPE, ARG_TYPE_CREATE) }
            )
        }
        // bottomNavigation与viewPager联动绑定
        binding.navigationBar.bindViewPager(binding.vpContainer)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun LifecycleCoroutineScope.bindData() {
        createTodoPages()
        bindFragmentResultListener()

        launchWhenResumed {
            viewModel.viewStates.collectState(TodoViewState::titleRes) {
                binding.toolbar.setTitleText(getString(it))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.vpContainer.adapter = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun createTodoPages() {
        val fragments = arrayListOf<Fragment>().apply {
            add(TodoListFragment.newInstance(ARG_STATUS_UPCOMING))
            add(TodoListFragment.newInstance(ARG_STATUS_COMPLETE))
        }

        if (binding.vpContainer.adapter == null) {
            mAdapter = UiPagerAdapter2(childFragmentManager, viewLifecycleOwner.lifecycle)
            mAdapter?.addAll(fragments)
            binding.vpContainer.adapter = mAdapter
            binding.vpContainer.isUserInputEnabled = false
        } else {
            mAdapter?.clear()
            mAdapter?.addAll(fragments)
            mAdapter?.notifyDataSetChanged()
        }
    }

    private fun bindFragmentResultListener() {
        val listener = fun(requestKey: String, bundle: Bundle) {
            val todo = bundle.getParcelable<TodoData>(REQUEST_VALUE_TODO)
            val type = bundle.getInt(REQUEST_VALUE_TYPE)
            childFragmentManager.fragments.forEach {
                val actionListener = it as? TodoActionListener
                when (requestKey) {
                    REQUEST_KEY_SAVE -> actionListener?.addAction(todo)
                    REQUEST_KEY_UPDATE -> actionListener?.updateAction(todo)
                    REQUEST_KEY_TYPE -> {
                        viewModel.dispatch(TodoViewAction.ChangeTypeSelected(type))
                        actionListener?.notifyAction(type)
                    }
                }
            }
        }

        setFragmentResultListener(REQUEST_KEY_SAVE, listener)
        setFragmentResultListener(REQUEST_KEY_UPDATE, listener)
        setFragmentResultListener(REQUEST_KEY_TYPE, listener)
    }

    /**
     * 供子Fragment调用互通
     */
    fun moveTodoItem(todo: TodoData) {
        childFragmentManager.fragments.forEach { (it as? TodoActionListener)?.moveAction(todo) }
    }

}