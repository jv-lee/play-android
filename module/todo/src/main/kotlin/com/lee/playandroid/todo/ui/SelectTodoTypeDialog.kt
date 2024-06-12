package com.lee.playandroid.todo.ui

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.lee.playandroid.base.base.BaseBindingAlertFragment
import com.lee.playandroid.base.extensions.collectState
import com.lee.playandroid.base.widget.WheelView
import com.lee.playandroid.todo.databinding.DialogSelectTodoBinding
import com.lee.playandroid.todo.model.entity.TodoType
import com.lee.playandroid.todo.model.entity.TodoTypeData
import com.lee.playandroid.todo.ui.TodoFragment.Companion.REQUEST_KEY_TYPE
import com.lee.playandroid.todo.ui.TodoFragment.Companion.REQUEST_VALUE_TYPE
import com.lee.playandroid.todo.viewmodel.SelectTodoTypeViewModel
import com.lee.playandroid.todo.viewmodel.SelectTodoTypeViewState

/**
 * Todo列表类型选择器弹窗
 * @author jv.lee
 * @date 2022/1/2
 */
class SelectTodoTypeDialog :
    BaseBindingAlertFragment<DialogSelectTodoBinding>(){

    private val viewModel by viewModels<SelectTodoTypeViewModel>()

    private var type = TodoType.DEFAULT

    override fun bindView() {
        mBinding.root.setOnClickListener { dismiss() }
    }

    override fun bindData() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            // 监听todoType列表数据实时构建当前滑动选择类标
            viewModel.viewStates.collectState(SelectTodoTypeViewState::todoTypeWheelData) {
                mBinding.wheelView.bindData(
                    it.todoTypes,
                    object : WheelView.DataFormat<TodoTypeData> {
                        override fun format(item: TodoTypeData) = item.name
                    },
                    object : WheelView.SelectedListener<TodoTypeData> {
                        override fun selected(item: TodoTypeData) {
                            type = item.type
                        }
                    },
                    startPosition = it.startIndex
                )
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // 弹窗关闭时回传当前选中todoType通知页面更新
        setFragmentResult(
            REQUEST_KEY_TYPE,
            Bundle().apply { putInt(REQUEST_VALUE_TYPE, type) }
        )
    }
}