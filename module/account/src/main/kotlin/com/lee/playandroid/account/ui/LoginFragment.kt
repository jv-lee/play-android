package com.lee.playandroid.account.ui

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.toast
import com.lee.library.mvvm.ui.observeState
import com.lee.library.tools.PreferencesTools
import com.lee.library.utils.LogUtil
import com.lee.playandroid.account.R
import com.lee.playandroid.account.constants.Constants
import com.lee.playandroid.account.databinding.FragmentLoginBinding
import com.lee.playandroid.account.viewmodel.AccountViewModel
import com.lee.playandroid.account.viewmodel.LoginRegisterViewModel
import com.lee.playandroid.library.common.constants.Constants.REQUEST_KEY
import com.lee.playandroid.library.common.entity.AccountData

/**
 * @author jv.lee
 * @date 2021/11/24
 * @description
 */
class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private val viewModel by viewModels<LoginRegisterViewModel>()
    private val accountViewModel by activityViewModels<AccountViewModel>()

    private val binding by binding(FragmentLoginBinding::bind)

    override fun bindView() {
        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_login_fragment_to_register_fragment)
        }
        binding.buttonLogin.setOnClickListener {

        }
    }

    override fun bindData() {
        setFragmentResultListener(REQUEST_KEY) { _: String, _: Bundle ->
            findNavController().popBackStack()
        }

        viewModel.accountLive.observeState<AccountData>(this, success = {
            accountViewModel.updateAccountInfo(it)
            findNavController().popBackStack()
        }, error = {
            LogUtil.i("login error")
            toast(it.message)
        })
    }

}