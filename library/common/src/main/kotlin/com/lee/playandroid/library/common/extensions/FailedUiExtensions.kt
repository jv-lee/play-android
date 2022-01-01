package com.lee.playandroid.library.common.extensions

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

fun FragmentActivity.actionFailed(throwable: Throwable) {
    if(isDestroyed) return
    if (NetworkUtil.isConnected(applicationContext)) {
        toast(HttpManager.getInstance().getServerMessage(throwable, "errorMsg"))
    } else {
        toast(getString(R.string.network_not_access))
    }
}

fun Fragment.actionFailed(throwable: Throwable) {
    if (!isAdded) return
    if (NetworkUtil.isConnected(requireContext().applicationContext)) {
        toast(HttpManager.getInstance().getServerMessage(throwable, "errorMsg"))
    } else {
        toast(getString(R.string.network_not_access))
    }
}