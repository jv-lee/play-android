/*
 * 项目错误ui处理公共扩展函数
 * @author jv.lee
 * @date 2021/12/9
 */
package com.lee.playandroid.common.extensions

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.lee.playandroid.base.extensions.toast
import com.lee.playandroid.base.net.HttpManager
import com.lee.playandroid.base.utils.NetworkUtil
import com.lee.playandroid.base.widget.SnackBarEx
import com.lee.playandroid.common.R

private const val EXTENSIONS_KEY_THROWABLE_CODE = "extensionsKey:throwableCode"

var FragmentActivity.saveThrowableCode: Int
    get() = intent?.getIntExtra(EXTENSIONS_KEY_THROWABLE_CODE, 0) ?: 0
    set(value) {
        intent.putExtra(EXTENSIONS_KEY_THROWABLE_CODE, value)
    }

var Fragment.saveThrowableCode: Int
    get() = arguments?.getInt(EXTENSIONS_KEY_THROWABLE_CODE, 0) ?: 0
    set(value) {
        arguments?.putInt(EXTENSIONS_KEY_THROWABLE_CODE, value) ?: kotlin.run {
            arguments = Bundle().apply {
                putInt(EXTENSIONS_KEY_THROWABLE_CODE, value)
            }
        }
    }

fun FragmentActivity.actionFailed(throwable: Throwable) {
    if (isDestroyed) return
    if (NetworkUtil.isNetworkConnected(applicationContext)) {
        val throwableCode = throwable.hashCode()
        if (throwableCode != saveThrowableCode) {
            saveThrowableCode = throwableCode
            toast(HttpManager.instance.getServerMessage(throwable, "errorMsg"))
        }
    } else {
        SnackBarEx.Builder(findViewById(android.R.id.content))
            .setMessage(getString(R.string.network_not_access))
            .build()
            .show()
    }
}

fun Fragment.actionFailed(throwable: Throwable) {
    if (!isAdded) return
    if (NetworkUtil.isNetworkConnected(requireContext().applicationContext)) {
        val throwableCode = throwable.hashCode()
        if (throwableCode != saveThrowableCode) {
            saveThrowableCode = throwableCode
            toast(HttpManager.instance.getServerMessage(throwable, "errorMsg"))
        }
    } else {
        SnackBarEx.Builder(requireActivity().findViewById(android.R.id.content))
            .setMessage(getString(R.string.network_not_access))
            .build()
            .show()
    }
}