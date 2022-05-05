package com.lee.playandroid.me.viewmodel

import androidx.lifecycle.ViewModel
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService

/**
 * @author jv.lee
 * @date 2022/4/29
 * @description
 */
class SettingsViewModel : ViewModel() {

    val accountService = ModuleService.find<AccountService>()
}