package com.lee.playandroid.me.viewmodel

import androidx.lifecycle.ViewModel
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService

/**
 *
 * @author jv.lee
 * @date 2022/5/5
 */
class MeViewModel : ViewModel() {
    val accountService: AccountService = ModuleService.find()
}