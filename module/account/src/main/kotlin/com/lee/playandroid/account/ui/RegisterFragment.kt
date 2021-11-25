package com.lee.playandroid.account.ui

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.toast
import com.lee.library.mvvm.ui.observeState
import com.lee.playandroid.account.R
import com.lee.playandroid.account.databinding.FragmentRegisterBinding
import com.lee.playandroid.account.viewmodel.AccountViewModel
import com.lee.playandroid.account.viewmodel.LoginRegisterViewModel
import com.lee.playandroid.library.common.constants.Constants.REQUEST_KEY
import com.lee.playandroid.library.common.entity.AccountData

/**
 * @author jv.lee
 * @date 2021/11/24
 * @description
 */
class RegisterFragment : BaseFragment(R.layout.fragment_register) {

    private val viewModel by viewModels<LoginRegisterViewModel>()
    private val accountViewModel by activityViewModels<AccountViewModel>()

    private val binding by binding(FragmentRegisterBinding::bind)

    override fun bindView() {
        binding.button.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun bindData() {
        viewModel.accountLive.observeState<AccountData>(this, success = {
            accountViewModel.updateAccountInfo(it)
            setFragmentResult(REQUEST_KEY, Bundle.EMPTY)
            findNavController().popBackStack()
        }, error = {
            toast(it.message)
        })
    }
}