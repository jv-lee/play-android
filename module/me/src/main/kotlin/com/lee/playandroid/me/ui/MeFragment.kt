package com.lee.playandroid.me.ui

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.delayBackEvent
import com.lee.library.mvvm.ui.observeState
import com.lee.library.utils.LogUtil
import com.lee.playandroid.library.common.entity.AccountData
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentMeBinding
import com.lee.playandroid.me.viewmodel.MeViewModel
import com.lee.playandroid.router.navigateLogin

/**
 * @author jv.lee
 * @data 2021/11/2
 * @description
 */
class MeFragment : BaseFragment(R.layout.fragment_me) {

    private val viewModel by viewModels<MeViewModel>()

    private val binding by binding(FragmentMeBinding::bind)

    private val accountService = ModuleService.find<AccountService>()

    override fun bindView() {
        delayBackEvent()

        binding.toolbarLayout.setOnClickListener {
            findNavController().navigateLogin()
        }
    }

    override fun bindData() {
        accountService.getAccountLive(requireActivity())
            .observeState<AccountData>(viewLifecycleOwner, success = {
                LogUtil.i("setUes Account:$it")
            }, default = {
                LogUtil.i("setDefault Account")
            }, error = {
                LogUtil.i("setError Account")
            })
    }

}