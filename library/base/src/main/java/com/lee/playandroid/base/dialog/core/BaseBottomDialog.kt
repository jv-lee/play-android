package com.lee.playandroid.base.dialog.core

import android.content.Context
import com.lee.playandroid.base.R
import com.lee.playandroid.base.dialog.extensions.setBottomDialog

/**
 * 使用 setBottomDialog(260) 限制子类最大高度 不设置limitHeight默认为全屏
 * @author jv.lee
 * @date 2020/9/10
 */
abstract class BaseBottomDialog constructor(
    context: Context,
    layoutId: Int,
    isCancel: Boolean = true,
    limitHeight: Int = 0
) : BaseDialog(context, R.style.BottomDialogTheme, layoutId, isCancel) {

    init {
        setBottomDialog(limitHeight)
    }
}