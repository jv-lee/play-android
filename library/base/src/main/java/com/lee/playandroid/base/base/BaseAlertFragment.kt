@file:Suppress("LeakingThis")

package com.lee.playandroid.base.base

import com.lee.playandroid.base.R

/**
 * AlertDialogFragment通用基类 带Alert缩放动画
 * @author jv.lee
 * @date 2020/9/21
 */
abstract class BaseAlertFragment(resourceId: Int, isCancel: Boolean = true) :
    BaseDialogFragment(resourceId, isCancel) {
    init {
        setStyle(STYLE_NO_TITLE, R.style.BaseAlertDialog)
    }
}