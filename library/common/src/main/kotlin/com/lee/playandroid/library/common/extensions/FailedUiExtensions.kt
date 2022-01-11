package com.lee.playandroid.library.common.extensions

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.lee.library.extensions.toast
import com.lee.library.net.HttpManager
import com.lee.library.utils.NetworkUtil
import com.lee.playandroid.library.common.R

/**
 * @author jv.lee
 * @date 2021/12/9
 * @description
 */

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
    if (NetworkUtil.isConnected(applicationContext)) {
        val throwableCode = throwable.hashCode()
        if (throwableCode != saveThrowableCode) {
            saveThrowableCode = throwableCode
            toast(HttpManager.getInstance().getServerMessage(throwable, "errorMsg"))
        }
    } else {
        toast(getString(R.string.network_not_access))
    }
}

fun Fragment.actionFailed(throwable: Throwable) {
    if (!isAdded) return
    if (NetworkUtil.isConnected(requireContext().applicationContext)) {
        val throwableCode = throwable.hashCode()
        if (throwableCode != saveThrowableCode) {
            saveThrowableCode = throwableCode
            toast(HttpManager.getInstance().getServerMessage(throwable, "errorMsg"))
        }
    } else {
        toast(getString(R.string.network_not_access))
    }
}