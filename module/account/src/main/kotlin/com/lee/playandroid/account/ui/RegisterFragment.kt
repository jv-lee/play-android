package com.lee.playandroid.account.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.keyboardObserver
import com.lee.library.extensions.toast
import com.lee.library.interadp.TextWatcherAdapter
import com.lee.library.mvvm.ui.observeState
import com.lee.library.tools.KeyboardTools
import com.lee.playandroid.account.R
import com.lee.playandroid.account.databinding.FragmentRegisterBinding
import com.lee.playandroid.account.viewmodel.AccountViewModel
import com.lee.playandroid.account.viewmodel.LoginRegisterViewModel
import com.lee.playandroid.library.common.constants.Constants.REQUEST_KEY
import com.lee.playandroid.library.common.entity.AccountData

/**
 * @author jv.lee
 * @date 2021/11/24
 * @description 注册页面
 */
class RegisterFragment : BaseFragment(R.layout.fragment_register), View.OnClickListener,
    TextWatcherAdapter {

    private val viewModel by viewModels<LoginRegisterViewModel>()
    private val accountViewModel by activityViewModels<AccountViewModel>()

    private val binding by binding(FragmentRegisterBinding::bind)

    override fun bindView() {
        binding.constRoot.keyboardObserver { diff ->
            if (isResumed) {
                binding.constRoot.updatePadding(bottom = diff)
            }
        }

        binding.tvLogin.setOnClickListener(this)
        binding.tvRegister.setOnClickListener(this)
        binding.editUsername.addTextChangedListener(this)
        binding.editPassword.addTextChangedListener(this)
        binding.editRePassword.addTextChangedListener(this)
    }

    override fun bindData() {
        viewModel.accountLive.observeState<AccountData>(viewLifecycleOwner, success = {
            accountViewModel.updateAccountInfo(it)
            setFragmentResult(REQUEST_KEY, Bundle.EMPTY)
            findNavController().popBackStack()
        }, error = {
            toast(it.message)
        })
    }

    override fun onClick(view: View) {
        when (view) {
            binding.tvRegister -> {
                viewModel.requestRegister(
                    binding.editUsername.text.toString(),
                    binding.editPassword.text.toString(),
                    binding.editRePassword.text.toString()
                )
            }
            binding.tvLogin -> {
                if (binding.constRoot.paddingBottom > 10) {
                    KeyboardTools.hideSoftInput(requireActivity())
                } else {
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (binding.editUsername.text.toString().isEmpty() ||
            binding.editPassword.text.toString().isEmpty() ||
            binding.editRePassword.text.toString().isEmpty()
        ) {
            binding.tvLogin.setButtonDisable(true)
        } else {
            binding.tvLogin.setButtonDisable(false)
        }
    }

}