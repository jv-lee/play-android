package com.lee.playandroid.todo.ui

import android.app.DatePickerDialog
import android.os.Build
import android.widget.DatePicker
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.base.base.BaseBindingNavigationFragment
import com.lee.playandroid.base.dialog.LoadingDialog
import com.lee.playandroid.base.extensions.arguments
import com.lee.playandroid.base.extensions.argumentsOrNull
import com.lee.playandroid.base.extensions.collectState
import com.lee.playandroid.base.extensions.dismiss
import com.lee.playandroid.base.extensions.show
import com.lee.playandroid.base.extensions.toast
import com.lee.playandroid.base.interadp.TextWatcherAdapter
import com.lee.playandroid.base.tools.SystemBarTools.hasSoftInputShow
import com.lee.playandroid.base.tools.SystemBarTools.hideSoftInput
import com.lee.playandroid.base.tools.SystemBarTools.parentTouchHideSoftInput
import com.lee.playandroid.base.tools.SystemBarTools.softInputBottomPaddingChange
import com.lee.playandroid.common.entity.TodoData
import com.lee.playandroid.common.entity.TodoData.Companion.PRIORITY_HEIGHT
import com.lee.playandroid.common.entity.TodoData.Companion.PRIORITY_LOW
import com.lee.playandroid.common.extensions.actionFailed
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.FragmentCreateTodoBinding
import com.lee.playandroid.todo.viewmodel.CreateTodoViewEvent
import com.lee.playandroid.todo.viewmodel.CreateTodoViewIntent
import com.lee.playandroid.todo.viewmodel.CreateTodoViewModel
import com.lee.playandroid.todo.viewmodel.CreateTodoViewState
import java.util.Calendar

/**
 * 创建todo/编辑todo页面
 * @author jv.lee
 * @date 2021/12/28
 */
class CreateTodoFragment :
    BaseBindingNavigationFragment<FragmentCreateTodoBinding>(),
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

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    private var datePickerDialog: DatePickerDialog? = null

    override fun bindView() {
        // 设置点击空白区域隐藏软键盘
        requireActivity().window.parentTouchHideSoftInput()

        // 监听键盘弹起
        mBinding.root.softInputBottomPaddingChange()

        mBinding.tvDateContent.setOnClickListener {
            datePickerDialog?.let(this::show)
        }

        // 保存TODO点击事件
        mBinding.tvSave.setOnClickListener {
            viewModel.dispatch(CreateTodoViewIntent.RequestPostTodo)
        }

        mBinding.editTitle.addTextChangedListener(object : TextWatcherAdapter {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.dispatch(CreateTodoViewIntent.ChangeTitle(s?.toString() ?: ""))
            }
        })
        mBinding.editContent.addTextChangedListener(object : TextWatcherAdapter {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.dispatch(CreateTodoViewIntent.ChangeContent(s?.toString() ?: ""))
            }
        })
        mBinding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val priority =
                if (checkedId == mBinding.radioButtonLow.id) PRIORITY_LOW else PRIORITY_HEIGHT
            viewModel.dispatch(CreateTodoViewIntent.ChangePriority(priority))
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
                    mBinding.toolbar.setTitleText(getString(it))
                }
            }
            launchWhenResumed {
                collectState(CreateTodoViewState::title) {
                    mBinding.editTitle.setText(it)
                    mBinding.editTitle.setSelection(it.length)
                }
            }
            launchWhenResumed {
                collectState(CreateTodoViewState::content) {
                    mBinding.editContent.setText(it)
                    mBinding.editContent.setSelection(it.length)
                }
            }
            launchWhenResumed {
                collectState(CreateTodoViewState::priority) {
                    mBinding.radioButtonLow.isChecked = it == PRIORITY_LOW
                    mBinding.radioButtonHeight.isChecked = it == PRIORITY_HEIGHT
                }
            }
            launchWhenResumed {
                collectState(CreateTodoViewState::date) {
                    mBinding.tvDateContent.text = it
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
        viewModel.dispatch(CreateTodoViewIntent.ChangeDate(date))
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