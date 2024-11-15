package com.lee.playandroid.base.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lee.playandroid.base.dialog.extensions.setBackDismiss
import com.lee.playandroid.base.dialog.extensions.setFullWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 通用DialogFragment基类
 * @author jv.lee
 * @date 2019/8/16.
 */
abstract class BaseDialogFragment(
    private val resourceId: Int? = 0,
    private val isCancel: Boolean = true,
    private val isFullWindow: Boolean = true
) : DialogFragment() {

    private var fistVisible = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return createView(inflater, container)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setBackDismiss(isCancel)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 全屏显示
        if (isFullWindow) dialog?.setFullWindow()
        bindView()
        bindData()
    }

    override fun onResume() {
        super.onResume()
        if (fistVisible) {
            fistVisible = false
            lazyLoad()
        }
    }

    open fun createView(inflater: LayoutInflater, container: ViewGroup?): View? {
        if (resourceId == null || resourceId == 0) throw RuntimeException(
            "fragment createView() not override && constructor params resourceId == 0"
        )
        return inflater.inflate(resourceId, container, false)
    }

    /**
     * 设置view基础配置
     */
    protected abstract fun bindView()

    /**
     * 设置加载数据等业务操作
     */
    protected abstract fun bindData()

    /**
     * 使用page 多fragment时 懒加载
     */
    open fun lazyLoad() {}

    fun launchOnLifecycle(
        state: Lifecycle.State = Lifecycle.State.CREATED,
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state, block)
        }
    }

}