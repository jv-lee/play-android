package com.lee.playandroid.account.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.library.tools.PreferencesTools
import com.lee.library.utils.LogUtil
import com.lee.playandroid.account.constants.Constants
import com.lee.playandroid.account.model.repository.ApiRepository
import com.lee.playandroid.library.common.entity.AccountData
import com.lee.playandroid.library.common.extensions.checkData

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description 账户信息ViewModel 作用于全局 - 基于MainActivity
 * 其他模块通过 ModuleService.find<AccountService>().getAccountLive(requireActivity()) 获取LiveData进行账户数据监听
 */
class AccountViewModel : CoroutineViewModel() {

    private val repository = ApiRepository()

    private val _accountLive = MutableLiveData<AccountData>()
    val accountLive: LiveData<AccountData> = _accountLive

    suspend fun requestAccountInfo() {
        val result = kotlin.runCatching {
            val account = repository.api.getAccountInfo().checkData()
            _accountLive.postValue(account)
        }
        PreferencesTools.put(Constants.KEY_IS_LOGIN, result.isSuccess)
    }

    fun loginUser() {

    }

    fun register() {

    }

    fun outLogin() {

    }

    init {
        LogUtil.i("initAccountViewModel")
    }

}