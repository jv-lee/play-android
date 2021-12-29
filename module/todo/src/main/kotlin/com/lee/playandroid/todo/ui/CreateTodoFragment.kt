package com.lee.playandroid.todo.ui

import android.os.Bundle
import androidx.core.view.updatePadding
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseFragment
import com.lee.library.dialog.LoadingDialog
import com.lee.library.extensions.*
import com.lee.library.mvvm.ui.observeState
import com.lee.library.tools.KeyboardTools
import com.lee.library.utils.TimeUtil
import com.lee.playandroid.library.common.entity.TodoData
import com.lee.playandroid.library.common.entity.TodoData.Companion.PRIORITY_HEIGHT
import com.lee.playandroid.library.common.entity.TodoData.Companion.PRIORITY_LOW
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.FragmentCreateTodoBinding
import com.lee.playandroid.todo.ui.TodoFragment.Companion.REQUEST_KEY_SAVE
import com.lee.playandroid.todo.ui.TodoFragment.Companion.REQUEST_KEY_UPDATE
import com.lee.playandroid.todo.ui.TodoFragment.Companion.REQUEST_VALUE_TODO
import com.lee.playandroid.todo.viewmodel.CreateTodoViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author jv.lee
 * @date 2021/12/28
 * @description 创建TODO页面
 */
class CreateTodoFragment : BaseFragment(R.layout.fragment_create_todo) {

    companion object {
        // 0：创建 1：编辑
        const val ARG_PARAMS_TYPE = "type"
        const val ARG_TYPE_CREATE = 0
        const val ARG_TYPE_EDIT = 1

        const val ARG_PARAMS_TODO = "todo"
    }

    private val type by arguments<Int>(ARG_PARAMS_TYPE)
    private val todoData by lazy<TodoData?> { arguments?.getParcelable(ARG_PARAMS_TODO) }

    private val viewModel by viewModels<CreateTodoViewModel>()

    private val binding by binding(FragmentCreateTodoBinding::bind)

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun bindView() {
        initViewData()

        // 设置键盘点击空白区取消
        KeyboardTools.parentTouchHideSoftInput(requireActivity(), binding.root)

        // 监听键盘弹起
        binding.root.keyboardObserver { diff ->
            if (isResumed) {
                binding.root.updatePadding(bottom = diff)
            }
        }

        // 保存TODO点击事件
        binding.tvSave.setOnClickListener {
            todoData?.apply { requestUpdateContent() } ?: kotlin.run { requestSaveContent() }
        }
    }

    override fun bindData() {
        viewModel.todoLive.observeState<TodoData>(this, success = {
            responseTodo(it)
            dismiss(loadingDialog)
            findNavController().popBackStack()
        }, error = {
            dismiss(loadingDialog)
            actionFailed(it)
        }, loading = {
            show(loadingDialog)
        })
    }

    /**
     * 根据数据渲染基础ui
     */
    private fun initViewData() {
        // 设置Toolbar标题
        binding.toolbar.setTitleText(
            getString(
                if (type == ARG_TYPE_CREATE) R.string.title_create_todo else R.string.title_edit_todo
            )
        )

        // 根据数据设置
        todoData?.apply {
            // 设置TODO标题及内容
            binding.editTitle.setText(title)
            binding.editContent.setText(content)

            // 设置优先级
            if (priority == PRIORITY_LOW) {
                binding.radioButtonLow.isChecked = true
            } else {
                binding.radioButtonHeight.isChecked = true
            }

            // 设置时间Format
            binding.tvDateContent.text = dateStr
        } ?: kotlin.run {
            // 设置默认值
            binding.radioButtonLow.isChecked = true

            // 获取当前时间format
            val currentDate: String =
                TimeUtil.date2String(Date(), SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()))
            binding.tvDateContent.text = currentDate
        }
    }

    /**
     * 请求创建TODO
     */
    private fun requestSaveContent() {
        val title = binding.editTitle.text.toString()
        val content = binding.editContent.text.toString()
        val date = binding.tvDateContent.text.toString()
        val priority = if (binding.radioButtonLow.isChecked) PRIORITY_LOW else PRIORITY_HEIGHT
        viewModel.requestAddTodo(title, content, date, priority)
    }

    /**
     * 请求更新TODO
     */
    private fun requestUpdateContent() {
        todoData?.apply {
            title = binding.editTitle.text.toString()
            content = binding.editContent.text.toString()
            dateStr = binding.tvDateContent.text.toString()
            priority = if (binding.radioButtonLow.isChecked) PRIORITY_LOW else PRIORITY_HEIGHT
            viewModel.requestUpdateTodo(this)
        }
    }

    /**
     * todo操作回调处理
     */
    private fun responseTodo(todoData: TodoData) {
        // 设置回调数据
        val resultKey = if (type == ARG_TYPE_CREATE) REQUEST_KEY_SAVE else REQUEST_KEY_UPDATE
        val bundle = Bundle().apply { putParcelable(REQUEST_VALUE_TODO, todoData) }
        setFragmentResult(resultKey, bundle)

        // 设置结果提示
        val message = if (type == ARG_TYPE_CREATE) getString(R.string.todo_create_success)
        else getString(R.string.todo_update_success)
        toast(message)
    }

}