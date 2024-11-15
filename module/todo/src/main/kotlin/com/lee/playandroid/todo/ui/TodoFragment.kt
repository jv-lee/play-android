@file:SuppressLint("NotifyDataSetChanged")

package com.lee.playandroid.todo.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.base.adapter.core.UiPagerAdapter2
import com.lee.playandroid.base.base.BaseBindingNavigationFragment
import com.lee.playandroid.base.extensions.collectState
import com.lee.playandroid.base.extensions.getParcelableCompat
import com.lee.playandroid.base.interadp.setClickListener
import com.lee.playandroid.common.entity.TodoData
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.FragmentTodoBinding
import com.lee.playandroid.todo.ui.CreateTodoFragment.Companion.ARG_PARAMS_TYPE
import com.lee.playandroid.todo.ui.CreateTodoFragment.Companion.ARG_TYPE_CREATE
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_STATUS_COMPLETE
import com.lee.playandroid.todo.ui.TodoListFragment.Companion.ARG_STATUS_UPCOMING
import com.lee.playandroid.todo.ui.listener.TodoActionListener
import com.lee.playandroid.todo.viewmodel.TodoViewIntent
import com.lee.playandroid.todo.viewmodel.TodoViewModel
import com.lee.playandroid.todo.viewmodel.TodoViewState

/**
 * todo页面（待完成/已完成）
 * @author jv.lee
 * @date 2021/12/23
 */
class TodoFragment : BaseBindingNavigationFragment<FragmentTodoBinding>() {

    companion object {
        /** 回传请求key：通知TODO数据添加 */
        const val REQUEST_KEY_SAVE = "requestKey:save"

        /** 回传请求key：通知TODO数据更新 */
        const val REQUEST_KEY_UPDATE = "requestKey:update"

        /** 回传请求key：通知TODO类型更新 */
        const val REQUEST_KEY_TYPE = "requestKey:type"

        /** 回传数据请求key：通知TODO变更数据结果 */
        const val REQUEST_VALUE_TODO = "requestValue:todo"

        /** 回传数据请求key：通知TODO类型变更数据结果 */
        const val REQUEST_VALUE_TYPE = "requestValue:type"
    }

    private val viewModel by viewModels<TodoViewModel>()

    private var mAdapter: UiPagerAdapter2? = null

    override fun bindView() {
        mBinding.toolbar.setClickListener {
            // 导航至todo类型选择弹窗
            moreClick {
                findNavController().navigate(
                    R.id.action_todo_fragment_to_select_todo_type_dialog
                )
            }
        }
        mBinding.floatingButton.setOnClickListener {
            // 导航至todo编辑页面
            findNavController().navigate(
                R.id.action_todo_fragment_to_create_todo_fragment,
                Bundle().apply { putInt(ARG_PARAMS_TYPE, ARG_TYPE_CREATE) }
            )
        }
        // bottomNavigation与viewPager联动绑定
        mBinding.navigationBar.bindViewPager(mBinding.vpContainer)
    }

    override fun LifecycleCoroutineScope.bindData() {
        createTodoPages()
        bindFragmentResultListener()

        launchOnLifecycle {
            // 监听当前标题状态实时更新标题显示
            viewModel.viewStates.collectState(TodoViewState::titleRes) {
                mBinding.toolbar.setTitleText(getString(it))
            }
        }
    }

    override fun onDestroyView() {
        mBinding.vpContainer.adapter = null
        super.onDestroyView()
    }

    /**
     * 构建todoViewPager页面（完成page/未完成page）
     */
    private fun createTodoPages() {
        val fragments = arrayListOf<Fragment>().apply {
            add(TodoListFragment.newInstance(ARG_STATUS_UPCOMING))
            add(TodoListFragment.newInstance(ARG_STATUS_COMPLETE))
        }

        if (mBinding.vpContainer.adapter == null) {
            mAdapter = UiPagerAdapter2(childFragmentManager, viewLifecycleOwner.lifecycle)
            mAdapter?.addAll(fragments)
            mBinding.vpContainer.adapter = mAdapter
            mBinding.vpContainer.isUserInputEnabled = false
        } else {
            mAdapter?.clear()
            mAdapter?.addAll(fragments)
            mAdapter?.notifyDataSetChanged()
        }
    }

    /**
     * 绑定todo处理后的结果处理监听
     */
    private fun bindFragmentResultListener() {
        // 页面回传状态变更监听函数
        val listener = fun(requestKey: String, bundle: Bundle) {
            // 回传的todo数据（save、update）
            val todo = bundle.getParcelableCompat<TodoData>(REQUEST_VALUE_TODO)
            // 回传的type数据 页面数据类型发生变更
            val type = bundle.getInt(REQUEST_VALUE_TYPE)
            childFragmentManager.fragments.forEach {
                val actionListener = it as? TodoActionListener
                when (requestKey) {
                    // todo添加后处理
                    REQUEST_KEY_SAVE -> actionListener?.addAction(todo)
                    // todo更新后处理
                    REQUEST_KEY_UPDATE -> actionListener?.updateAction(todo)
                    // todo类型变更后处理
                    REQUEST_KEY_TYPE -> {
                        viewModel.dispatch(TodoViewIntent.ChangeTypeSelected(type))
                        actionListener?.notifyAction(type)
                    }
                }
            }
        }

        // 绑定数据添加回传监听
        setFragmentResultListener(REQUEST_KEY_SAVE, listener)
        // 绑定数据更新回传监听
        setFragmentResultListener(REQUEST_KEY_UPDATE, listener)
        // 绑定TODO页面类型更新回传监听
        setFragmentResultListener(REQUEST_KEY_TYPE, listener)
    }

    /**
     * 供子Fragment调用互通
     * @param todoData todo数据
     */
    fun moveTodoItem(todoData: TodoData) {
        childFragmentManager.fragments.forEach { (it as? TodoActionListener)?.moveAction(todoData) }
    }
}