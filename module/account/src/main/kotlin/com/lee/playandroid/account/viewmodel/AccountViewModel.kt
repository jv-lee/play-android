package com.lee.playandroid.account.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.stateFlow
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.library.tools.PreferencesTools
import com.lee.library.utils.LogUtil
import com.lee.playandroid.account.constants.Constants
import com.lee.playandroid.account.model.repository.ApiRepository
import com.lee.playandroid.library.common.entity.AccountData
import com.lee.playandroid.library.common.extensions.checkData
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description 账户信息ViewModel 作用于全局 - 基于MainActivity
 * 其他模块通过 ModuleService.find<AccountService>().getAccountLive(requireActivity()) 获取LiveData进行账户数据监听
 */
class AccountViewModel : CoroutineViewModel() {

    private val repository = ApiRepository()

    private val _accountLive = MutableLiveData<UiState>()
    val accountLive: LiveData<UiState> = _accountLive

    suspend fun requestAccountInfo() {
        stateFlow {
            repository.api.getAccountInfo().apply {
                PreferencesTools.put(Constants.SP_KEY_IS_LOGIN, errorCode == 0)
            }.checkData()
        }.collect {
            _accountLive.postValue(it)
        }
    }

    fun updateAccountInfo(accountData: AccountData) {
        PreferencesTools.put(Constants.SP_KEY_IS_LOGIN, true)
        _accountLive.postValue(UiState.Success(accountData))
    }

    fun getAccountInfo(): AccountData? {
        val item = _accountLive.value
        if (item is UiState.Success<*>) {
            return item.data as? AccountData
        }
        return null
    }

    fun requestLogout(
        showLoading: () -> Unit = {},
        hideLoading: () -> Unit = {},
        failedCall: (String) -> Unit = {}
    ) {
        launchMain {
            showLoading()
            val response = withIO { repository.api.requestLogout() }
            if (response.errorCode == 0) {
                _accountLive.postValue(UiState.Default)
                PreferencesTools.put(Constants.SP_KEY_IS_LOGIN, false)
            } else {
                failedCall(response.errorMsg)
            }
            hideLoading()
        }
    }

    init {
        LogUtil.i("initAccountViewModel")
    }

}