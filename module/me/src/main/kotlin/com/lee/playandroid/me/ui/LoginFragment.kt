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

    override fun bindView() {
        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_login_fragment_to_register_fragment)
        }

        setFragmentResultListener("key") { requestKey: String, bundle: Bundle ->
            findNavController().popBackStack()
        }
    }

    override fun bindData() {

    }

}