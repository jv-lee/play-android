package com.lee.playandroid.me.ui

import android.os.Bundle
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentRegisterBinding

/**
 * @author jv.lee
 * @date 2021/11/24
 * @description
 */
class RegisterFragment : BaseFragment(R.layout.fragment_register) {

    private val binding by binding(FragmentRegisterBinding::bind)

    override fun bindView() {
        binding.button.setOnClickListener {
            setFragmentResult("key", Bundle.EMPTY)
            findNavController().popBackStack()
        }
    }

    override fun bindData() {

    }
}