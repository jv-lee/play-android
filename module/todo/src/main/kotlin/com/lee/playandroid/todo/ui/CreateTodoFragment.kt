package com.lee.playandroid.todo.ui

import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.keyboardObserver
import com.lee.library.tools.KeyboardTools
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.FragmentCreateTodoBinding
import com.lee.playandroid.todo.viewmodel.CreateTodoViewModel

/**
 * @author jv.lee
 * @date 2021/12/28
 * @description 创建TODO页面
 */
class CreateTodoFragment : BaseFragment(R.layout.fragment_create_todo) {

    private val viewModel by viewModels<CreateTodoViewModel>()

    private val binding by binding(FragmentCreateTodoBinding::bind)

    override fun bindView() {
        KeyboardTools.parentTouchHideSoftInput(requireActivity(), binding.root)

        // 监听键盘弹起
        binding.root.keyboardObserver { diff ->
            if (isResumed) {
                binding.root.updatePadding(bottom = diff)
            }
        }
    }

    override fun bindData() {

    }
}