package com.lee.playandroid.todo.ui

import android.app.DatePickerDialog
import android.os.Build
import android.widget.DatePicker
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.base.base.BaseNavigationFragment
import com.lee.playandroid.base.dialog.LoadingDialog
import com.lee.playandroid.base.extensions.*
import com.lee.playandroid.base.interadp.TextWatcherAdapter
import com.lee.playandroid.base.tools.SystemBarTools.hasSoftInputShow
import com.lee.playandroid.base.tools.SystemBarTools.hideSoftInput
import com.lee.playandroid.base.tools.SystemBarTools.parentTouchHideSoftInput
import com.lee.playandroid.base.tools.SystemBarTools.softInputBottomPaddingChange
import com.lee.playandroid.base.viewstate.collectState
import com.lee.playandroid.common.entity.TodoData
import com.lee.playandroid.common.entity.TodoData.Companion.PRIORITY_HEIGHT
import com.lee.playandroid.common.entity.TodoData.Companion.PRIORITY_LOW
import com.lee.playandroid.common.extensions.actionFailed
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.FragmentCreateTodoBinding
import com.lee.playandroid.todo.viewmodel.CreateTodoViewAction
import com.lee.playandroid.todo.viewmodel.CreateTodoViewEvent
import com.lee.playandroid.todo.viewmodel.CreateTodoViewModel
import com.lee.playandroid.todo.viewmodel.CreateTodoViewState
import java.util.*

/**
 * 创建todo/编辑todo页面
 * @author jv.lee
 * @date 2021/12/28
 */
class CreateTodoFragment :
    BaseNavigationFragment(R.layout.fragment_create_todo),
    DatePickerDialog.OnDateSetListener {

    companion object {
        /** 当前页面类型 创建TODO:[ARG_TYPE_CREATE]  编辑TODO:[ARG_TYPE_EDIT] */
        const val ARG_PARAMS_TYPE = "type"
        const val ARG_TYPE_CREATE = 0
        const val ARG_TYPE_EDIT = 1

        /** 编辑状态传入的todo数据 */
        const val ARG_PARAMS_TODO = "todo"
    }

    private val type by arguments<Int>(ARG_PARAMS_TYPE)
    private val todoData by argumentsOrNull<TodoData>(ARG_PARAMS_TODO)

    private val viewModel by viewModels<CreateTodoViewModel>(factoryProducer = {
        CreateTodoViewModel.CreateFactory(type, todoData)
    })

    private val binding by binding(FragmentCreateTodoBinding::bind)

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    private var datePickerDialog: DatePickerDialog? = null

    override fun bindView() {
        // 设置点击空白区域隐藏软键盘
        requireActivity().window.parentTouchHideSoftInput()

        // 监听键盘弹起
        binding.root.softInputBottomPaddingChange()

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

    override fun LifecycleCoroutineScope.bindData() {
        launchWhenResumed {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is CreateTodoViewEvent.RequestSuccess -> {
                        setFragmentResult(event.resultKey, event.bundle)
                        toast(event.message)
                        findNavController().popBackStack()
                    }
                    is CreateTodoViewEvent.RequestFailed -> {
                        actionFailed(event.error)
                    }
                }
            }
        }

        viewModel.viewStates.run {
            launchWhenResumed {
                collectState(CreateTodoViewState::isLoading) {
                    if (it) show(loadingDialog) else dismiss(loadingDialog)
                }
            }
            launchWhenResumed {
                collectState(CreateTodoViewState::appTitleRes) {
                    binding.toolbar.setTitleText(getString(it))
                }
            }
            launchWhenResumed {
                collectState(CreateTodoViewState::title) {
                    binding.editTitle.setText(it)
                    binding.editTitle.setSelection(it.length)
                }
            }
            launchWhenResumed {
                collectState(CreateTodoViewState::content) {
                    binding.editContent.setText(it)
                    binding.editContent.setSelection(it.length)
                }
            }
            launchWhenResumed {
                collectState(CreateTodoViewState::priority) {
                    binding.radioButtonLow.isChecked = it == PRIORITY_LOW
                    binding.radioButtonHeight.isChecked = it == PRIORITY_HEIGHT
                }
            }
            launchWhenResumed {
                collectState(CreateTodoViewState::date) {
                    binding.tvDateContent.text = it
                }
            }
            launchWhenResumed {
                collectState(CreateTodoViewState::calendar) { calendar ->
                    datePickerDialog = DatePickerDialog(
                        requireContext(),
                        R.style.ThemeDatePickerDialog,
                        this@CreateTodoFragment,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                }
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = "$year-${month + 1}-$dayOfMonth"
        viewModel.dispatch(CreateTodoViewAction.ChangeDate(date))
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            datePickerDialog?.setOnDateSetListener(this)
        }
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        if (requireActivity().window.hasSoftInputShow()) {
            requireActivity().window.hideSoftInput()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            datePickerDialog?.setOnDateSetListener(null)
        }
    }
}