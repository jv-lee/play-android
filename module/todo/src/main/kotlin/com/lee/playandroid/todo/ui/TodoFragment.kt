package com.lee.playandroid.todo.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.core.UiPagerAdapter2
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.binding
import com.lee.library.tools.PreferencesTools
import com.lee.library.widget.toolbar.TitleToolbar
import com.lee.playandroid.library.common.entity.TodoData
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.constants.Constants.SP_KEY_TODO_TYPE
import com.lee.playandroid.todo.databinding.FragmentTodoBinding
import com.lee.playandroid.todo.model.entity.TodoType
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
class TodoFragment : BaseNavigationFragment(R.layout.fragment_todo) {

    companion object {
        const val REQUEST_KEY_SAVE = "requestKey:save"
        const val REQUEST_KEY_UPDATE = "requestKey:update"
        const val REQUEST_KEY_TYPE = "requestKey:type"

        const val REQUEST_VALUE_TODO = "requestValue:todo"
        const val REQUEST_VALUE_TYPE = "requestValue:type"
    }

    private val binding by binding(FragmentTodoBinding::bind)

    private var mAdapter: UiPagerAdapter2? = null

    override fun bindView() {
        initTodoTitle()

        binding.navigationBar.bindViewPager(binding.vpContainer)

        binding.floatingButton.setOnClickListener {
            startCreateTodoPage()
        }
        binding.toolbar.setClickListener(object : TitleToolbar.ClickListener() {
            override fun moreClick() {
                findNavController().navigate(R.id.action_todo_fragment_to_select_todo_type_dialog)
            }
        })
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun LifecycleCoroutineScope.bindData() {
        createTodoPages()

        val listener = fun(requestKey: String, bundle: Bundle) {
            val todo = bundle.getParcelable<TodoData>(REQUEST_VALUE_TODO)
            val type = bundle.getInt(REQUEST_VALUE_TYPE)
            childFragmentManager.fragments.forEach {
                val actionListener = it as? TodoActionListener
                when (requestKey) {
                    REQUEST_KEY_SAVE -> actionListener?.addAction(todo)
                    REQUEST_KEY_UPDATE -> actionListener?.updateAction(todo)
                    REQUEST_KEY_TYPE -> {
                        initTodoTitle(type)
                        actionListener?.notifyAction(type)
                    }
                }
            }
        }

        setFragmentResultListener(REQUEST_KEY_SAVE, listener)
        setFragmentResultListener(REQUEST_KEY_UPDATE, listener)
        setFragmentResultListener(REQUEST_KEY_TYPE, listener)
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
            mAdapter?.addAll(fragments)
            mAdapter?.notifyDataSetChanged()
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

    /**
     * 根据type设置todo标题
     * @param type 当前todo类型
     */
    private fun initTodoTitle(
        type: Int = PreferencesTools.get(SP_KEY_TODO_TYPE, TodoType.DEFAULT)
    ) {
        val textResId = when (type) {
            TodoType.WORK -> R.string.todo_title_work
            TodoType.LIFE -> R.string.todo_title_life
            TodoType.PLAY -> R.string.todo_title_play
            else -> R.string.todo_title_default
        }
        binding.toolbar.setTitleText(getString(textResId))
    }

    /**
     * 供子Fragment调用互通
     */
    fun moveTodoItem(todo: TodoData) {
        childFragmentManager.fragments.forEach {
            (it as? TodoActionListener)?.moveAction(todo)
        }
    }

}