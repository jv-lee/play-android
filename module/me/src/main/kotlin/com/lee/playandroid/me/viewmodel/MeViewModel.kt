package com.lee.playandroid.me.viewmodel

import androidx.databinding.ObservableField
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.library.tools.DarkModeTools

/**
 * @author jv.lee
 * @date 2020/4/10
 * @description
 */
class MeViewModel : CoroutineViewModel() {

    val isSystem = ObservableField(DarkModeTools.get().isSystemTheme())
}