package com.lee.playandroid.account.ui

import android.os.Bundle
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.playandroid.account.R
import com.lee.playandroid.account.databinding.FragmentLoginBinding
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService

/**
 * @author jv.lee
 * @date 2021/11/24
 * @description
 */
class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private val binding by binding(FragmentLoginBinding::bind)

    companion object {
        const val REQUEST_KEY = "loginFragment-requestKey"
        const val ARG_PARAMS_REGISTER = "isRegister"
    }

    override fun bindView() {
        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_login_fragment_to_register_fragment)
        }
        binding.buttonLogin.setOnClickListener {
            ModuleService.find<AccountService>().loginUser(requireActivity())
        }
    }

    override fun bindData() {
        setFragmentResultListener(REQUEST_KEY) { _: String, bundle: Bundle ->
            if (bundle.getBoolean(ARG_PARAMS_REGISTER, false)) {
                findNavController().popBackStack()
            }
        }
    }

}