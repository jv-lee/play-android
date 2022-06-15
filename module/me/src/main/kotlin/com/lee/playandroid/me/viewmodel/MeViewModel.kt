package com.lee.playandroid.me.viewmodel

import androidx.lifecycle.ViewModel
import com.lee.playandroid.service.AccountService
import com.lee.playandroid.service.hepler.ModuleService

/**
 * MePage ViewModel
 * @author jv.lee
 * @date 2022/5/5
 */
class MeViewModel : ViewModel() {
    val accountService: AccountService = ModuleService.find()
}