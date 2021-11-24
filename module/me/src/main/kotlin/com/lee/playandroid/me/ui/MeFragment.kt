package com.lee.playandroid.me.ui

import androidx.fragment.app.viewModels
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.delayBackEvent
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentMeBinding
import com.lee.playandroid.me.viewmodel.MeViewModel

/**
 * @author jv.lee
 * @data 2021/11/2
 * @description
 */
class MeFragment : BaseFragment(R.layout.fragment_me) {

    private val viewModel by viewModels<MeViewModel>()

    private val binding by binding(FragmentMeBinding::bind)

    override fun bindView() {
        delayBackEvent()
    }

    override fun bindData() {
    }

}