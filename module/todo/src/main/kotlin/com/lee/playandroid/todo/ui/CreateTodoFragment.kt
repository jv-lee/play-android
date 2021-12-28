package com.lee.playandroid.todo.ui

import android.text.TextUtils
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.arguments
import com.lee.library.extensions.binding
import com.lee.library.extensions.keyboardObserver
import com.lee.library.tools.KeyboardTools
import com.lee.library.utils.TimeUtil
import com.lee.playandroid.todo.R
import com.lee.playandroid.todo.databinding.FragmentCreateTodoBinding
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

        // 0：一般 1：重要
        const val ARG_PARAMS_PRIORITY = "priority"
        const val ARG_PRIORITY_LOW = 0
        const val ARG_PRIORITY_HEIGHT = 1

        const val ARG_PARAMS_TITLE = "title"
        const val ARG_PARAMS_CONTENT = "content"
        const val ARG_PARAMS_DATE = "date"
    }

    private val type by arguments<Int>(ARG_PARAMS_TYPE)
    private val priority by arguments<Int>(ARG_PARAMS_PRIORITY)
    private val title by arguments<String>(ARG_PARAMS_TITLE)
    private val content by arguments<String>(ARG_PARAMS_CONTENT)
    private val date by arguments<String>(ARG_PARAMS_DATE)

    private val viewModel by viewModels<CreateTodoViewModel>()

    private val binding by binding(FragmentCreateTodoBinding::bind)

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
    }

    override fun bindData() {

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

        // 设置TODO标题及内容
        binding.editTitle.setText(title)
        binding.editContent.setText(content)

        // 设置优先级
        if (priority == ARG_PRIORITY_LOW) {
            binding.radioButtonLow.isChecked = true
        } else {
            binding.radioButtonHeight.isChecked = true
        }

        // 设置时间Format
        val currentDate =
            TimeUtil.date2String(Date(), SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()))
        binding.tvDateContent.text = if (TextUtils.isEmpty(date)) currentDate else date
    }

}