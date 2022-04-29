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
import com.lee.library.interadp.TextWatcherAdapter
import com.lee.library.tools.KeyboardTools.keyboardPaddingBottom
import com.lee.library.tools.KeyboardTools.parentTouchHideSoftInput
import com.lee.library.viewstate.collectState
import com.lee.playandroid.library.common.entity.TodoData
import com.lee.playandroid.library.common.entity.TodoData.Companion.PRIORITY_HEIGHT
import com.lee.playandroid.library.common.entity.TodoData.Companion.PRIORITY_LOW
import com.lee.playandroid.library.common.extensions.actionFailed
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.FragmentCreateTodoBinding
import com.lee.playandroid.todo.ui.TodoFragment.Companion.REQUEST_KEY_SAVE
import com.lee.playandroid.todo.ui.TodoFragment.Companion.REQUEST_KEY_UPDATE
import com.lee.playandroid.todo.ui.TodoFragment.Companion.REQUEST_VALUE_TODO
import com.lee.playandroid.todo.viewmodel.CreateTodoViewAction
import com.lee.playandroid.todo.viewmodel.CreateTodoViewEvent
import com.lee.playandroid.todo.viewmodel.CreateTodoViewModel
import com.lee.playandroid.todo.viewmodel.CreateTodoViewState
import kotlinx.coroutines.flow.collect
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

    private val viewModel by viewModels<CreateTodoViewModel>(factoryProducer = {
        CreateTodoViewModel.CreateFactory(
            todoData = todoData
        )
    })

    private val binding by binding(FragmentCreateTodoBinding::bind)

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    private var datePickerDialog: DatePickerDialog? = null

    override fun bindView() {
        // 设置键盘点击空白区取消
        requireActivity().parentTouchHideSoftInput(binding.root)

        requireActivity().window.decorView.keyboardPaddingBottom(viewLifecycleOwner)

        binding.tvDateContent.setOnClickListener {
            datePickerDialog?.let(this::show)
        }

        // 保存TODO点击事件
        binding.tvSave.setOnClickListener {
            viewModel.dispatch(CreateTodoViewAction.RequestPostTodo)
        }

        binding.editTitle.addTextChangedListener(object : TextWatcherAdapter {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.dispatch(CreateTodoViewAction.ChangeTitle(s?.toString() ?: ""))
            }
        })
        binding.editContent.addTextChangedListener(object : TextWatcherAdapter {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.dispatch(CreateTodoViewAction.ChangeContent(s?.toString() ?: ""))
            }
        })
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val priority =
                if (checkedId == binding.radioButtonLow.id) PRIORITY_LOW else PRIORITY_HEIGHT
            viewModel.dispatch(CreateTodoViewAction.ChangePriority(priority))
        }

    }

    override fun bindData() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is CreateTodoViewEvent.RequestSuccess -> {
                        responseTodo(event.todoData)
                        findNavController().popBackStack()
                    }
                    is CreateTodoViewEvent.RequestFailed -> {
                        actionFailed(event.error)
                    }
                }
            }
        }

        viewModel.viewStates.run {
            launchAndRepeatWithViewLifecycle {
                collectState(CreateTodoViewState::isLoading) {
                    if (it) show(loadingDialog) else dismiss(loadingDialog)
                }
            }
            launchAndRepeatWithViewLifecycle {
                collectState(CreateTodoViewState::appTitleRes) {
                    binding.toolbar.setTitleText(getString(it))
                }
            }
            launchAndRepeatWithViewLifecycle {
                collectState(CreateTodoViewState::title) {
                    binding.editTitle.setText(it)
                }
            }
            launchAndRepeatWithViewLifecycle {
                collectState(CreateTodoViewState::content) {
                    binding.editContent.setText(it)
                }
            }
            launchAndRepeatWithViewLifecycle {
                collectState(CreateTodoViewState::priority) {
                    binding.radioButtonLow.isChecked = it == PRIORITY_LOW
                    binding.radioButtonHeight.isChecked = it == PRIORITY_HEIGHT
                }
            }
            launchAndRepeatWithViewLifecycle {
                collectState(CreateTodoViewState::date) {
                    binding.tvDateContent.text = it
                }
            }
            launchAndRepeatWithViewLifecycle {
                collectState(CreateTodoViewState::calendar) { calendar ->
                    datePickerDialog = DatePickerDialog(
                        requireContext(), this@CreateTodoFragment,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                }
            }
        }
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            datePickerDialog?.setOnDateSetListener(this)
        }
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            datePickerDialog?.setOnDateSetListener(null)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = "$year-${month + 1}-$dayOfMonth"
        viewModel.dispatch(CreateTodoViewAction.ChangeDate(date))
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