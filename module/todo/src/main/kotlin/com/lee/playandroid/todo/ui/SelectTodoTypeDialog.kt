package com.lee.playandroid.todo.ui

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.lee.library.base.BaseAlertFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.toast
import com.lee.library.mvvm.ui.observeState
import com.lee.library.widget.WheelView
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.DialogSelectTodoBinding
import com.lee.playandroid.todo.model.entity.TodoType
import com.lee.playandroid.todo.model.entity.TodoTypeData
import com.lee.playandroid.todo.model.entity.TodoTypeWheelData
import com.lee.playandroid.todo.ui.TodoFragment.Companion.REQUEST_KEY_TYPE
import com.lee.playandroid.todo.ui.TodoFragment.Companion.REQUEST_VALUE_TYPE
import com.lee.playandroid.todo.viewmodel.SelectTodoTypeViewModel

/**
 * @author jv.lee
 * @date 2022/1/2
 * @description Todo列表类型选择器弹窗
 */
class SelectTodoTypeDialog :
    BaseAlertFragment(R.layout.dialog_select_todo) {

    private val binding by binding(DialogSelectTodoBinding::bind)

    private val viewModel by viewModels<SelectTodoTypeViewModel>()

    private var type = TodoType.DEFAULT

    override fun bindView() {
        binding.root.setOnClickListener { dismiss() }
    }

    override fun bindData() {
        viewModel.todoTypes.observeState<TodoTypeWheelData>(viewLifecycleOwner, success = {
            binding.wheelView.bindData(it.todoTypes, object : WheelView.DataFormat<TodoTypeData> {
                override fun format(item: TodoTypeData) = item.name
            }, object : WheelView.SelectedListener<TodoTypeData> {
                override fun selected(item: TodoTypeData) {
                    type = item.type
                }
            }, startPosition = it.startIndex)
        }, error = {
            toast(it.message)
        })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        setFragmentResult(REQUEST_KEY_TYPE, Bundle().apply {
            putInt(REQUEST_VALUE_TYPE, type)
        })
    }

}