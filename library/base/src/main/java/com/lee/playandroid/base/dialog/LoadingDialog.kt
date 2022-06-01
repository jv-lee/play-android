package com.lee.playandroid.base.dialog

import android.content.Context
import com.lee.playandroid.base.R
import com.lee.playandroid.base.dialog.core.BaseTranslucentDialog

/**
 *
 * @author jv.lee
 * @date 2020-03-07
 */
class LoadingDialog(context: Context) : BaseTranslucentDialog(context, R.layout.layout_dialog_loading,false) {

    override fun bindView() {}
}