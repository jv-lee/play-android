package com.lee.playandroid.me.ui

import android.os.Bundle
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentLoginBinding

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

        setFragmentResultListener(REQUEST_KEY) { _: String, bundle: Bundle ->
            if (bundle.getBoolean(ARG_PARAMS_REGISTER, false)) {
                findNavController().popBackStack()
            }
        }
    }

    override fun bindData() {

    }

}