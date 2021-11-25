package com.lee.playandroid.account.ui

import android.os.Bundle
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.playandroid.account.R
import com.lee.playandroid.account.databinding.FragmentRegisterBinding

/**
 * @author jv.lee
 * @date 2021/11/24
 * @description
 */
class RegisterFragment : BaseFragment(R.layout.fragment_register) {

    private val binding by binding(FragmentRegisterBinding::bind)

    override fun bindView() {
        binding.button.setOnClickListener {
            setFragmentResult(LoginFragment.REQUEST_KEY, Bundle.EMPTY)
            findNavController().popBackStack()
        }
    }

    override fun bindData() {

    }
}