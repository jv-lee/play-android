package com.lee.playandroid.system.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.system.ui.NavigationFragment
import com.lee.playandroid.system.ui.SystemContentFragment

/**
 * @author jv.lee
 * @date 2021/11/23
 * @description
 */
class SystemViewModel : CoroutineViewModel() {

    private val fragmentList: MutableList<Fragment> =
        mutableListOf(SystemContentFragment(), NavigationFragment())

    private val _fragmentsLive = MutableLiveData<MutableList<Fragment>>()
    val fragmentsLive: LiveData<MutableList<Fragment>> = _fragmentsLive

    private fun requestFragments() {
        _fragmentsLive.postValue(fragmentList)
    }

    init {
        requestFragments()
    }

}