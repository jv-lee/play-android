package com.lee.playandroid.todo.ui

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.dialog.LoadingDialog
import com.lee.library.extensions.*
import com.lee.library.mvvm.ui.stateObserve
import com.lee.library.tools.KeyboardTools.keyboardPaddingBottom
import com.lee.library.tools.KeyboardTools.parentTouchHideSoftInput
import com.lee.playandroid.library.common.entity.TodoData
import com.lee.playandroid.library.common.entity.TodoData.Companion.PRIORITY_HEIGHT
import com.lee.playandroid.library.common.entity.TodoData.Companion.PRIORITY_LOW
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.FragmentCreateTodoBinding
import com.lee.playandroid.todo.extensions.dateToStrFormat
import com.lee.playandroid.todo.extensions.stringToCalendar
import com.lee.playandroid.todo.ui.TodoFragment.Companion.REQUEST_KEY_SAVE
import com.lee.playandroid.todo.ui.TodoFragment.Companion.REQUEST_KEY_UPDATE
import com.lee.playandroid.todo.ui.TodoFragment.Companion.REQUEST_VALUE_TODO
import com.lee.playandroid.todo.viewmodel.CreateTodoViewModel
import java.util.*

/**
 * @author jv.lee
 * @date 2021/12/28
 * @description 创建TODO页面
 */
class CreateTodoFragment : BaseNavigationFragment(R.layout.fragment_create_todo),
    DatePickerDialog.OnDateSetListener {

    companion object {
        // 0：创建 1：编辑
        const val ARG_PARAMS_TYPE = "type"
        const val ARG_TYPE_CREATE = 0
        const val ARG_TYPE_EDIT = 1

        const val ARG_PARAMS_TODO = "todo"
    }

    private val type by arguments<Int>(ARG_PARAMS_TYPE)
    private val todoData by argumentsOrNull<TodoData>(ARG_PARAMS_TODO)

    private val viewModel by viewModels<CreateTodoViewModel>()

    private val binding by binding(FragmentCreateTodoBinding::bind)

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    private lateinit var datePickerDialog: DatePickerDialog

    override fun bindView() {
        initViewData()
        initDatePickerDialog()

        // 设置键盘点击空白区取消
        requireActivity().parentTouchHideSoftInput(binding.root)

        requireActivity().window.decorView.keyboardPaddingBottom(viewLifecycleOwner)

        binding.tvDateContent.setOnClickListener {
            show(datePickerDialog)
        }

        // 保存TODO点击事件
        binding.tvSave.setOnClickListener {
            todoData?.apply { requestUpdateContent() } ?: kotlin.run { requestSaveContent() }
        }
    }

    override fun bindData() {
        viewModel.todoLive.stateObserve<TodoData>(viewLifecycleOwner, success = {
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

    override fun onFragmentResume() {
        super.onFragmentResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            datePickerDialog.setOnDateSetListener(this)
        }
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            datePickerDialog.setOnDateSetListener(null)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = "$year-${month + 1}-$dayOfMonth"
        todoData?.apply { dateStr = date } ?: kotlin.run { binding.tvDateContent.text = date }
    }

    /**
     * 根据数据渲染基础ui
     */
    private fun initViewData() {
        // 设置Toolbar标题
        val toolbarTitle =
            getString(if (type == ARG_TYPE_CREATE) R.string.title_create_todo else R.string.title_edit_todo)
        binding.toolbar.setTitleText(toolbarTitle)

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
            binding.tvDateContent.text = dateToStrFormat()
        }
    }

    /**
     * 初始化时间选择dialog
     */
    private fun initDatePickerDialog() {
        val dateStr = todoData?.dateStr ?: dateToStrFormat()
        val calender = stringToCalendar(dateStr)
        datePickerDialog = DatePickerDialog(
            requireContext(),
            this,
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        )
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
            val updateTodoData = copy(
                title = binding.editTitle.text.toString(),
                content = binding.editContent.text.toString(),
                dateStr = binding.tvDateContent.text.toString(),
                priority = if (binding.radioButtonLow.isChecked) PRIORITY_LOW else PRIORITY_HEIGHT
            )
            viewModel.requestUpdateTodo(updateTodoData)
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