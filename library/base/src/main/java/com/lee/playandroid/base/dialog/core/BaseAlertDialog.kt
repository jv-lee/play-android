package com.lee.playandroid.base.dialog.core

import android.content.Context
import com.lee.playandroid.base.R

/**
 * 自定义dialog 超类
 * @author jv.lee
 * @date 2020-03-07
 */
abstract class BaseAlertDialog(
    context: Context,
    layoutId: Int,
    isCancel: Boolean = true
) :
    BaseDialog(context, R.style.BaseAlertDialog, layoutId, isCancel) {

    @JvmField
    var onConfirm: (()->Unit)? = null

    @JvmField
    var onCancel: (()->Unit)? = null
}