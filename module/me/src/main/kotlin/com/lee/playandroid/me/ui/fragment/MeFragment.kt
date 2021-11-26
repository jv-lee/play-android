package com.lee.playandroid.me.ui.fragment

import android.view.View
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.delayBackEvent
import com.lee.library.extensions.toast
import com.lee.library.mvvm.ui.observeState
import com.lee.playandroid.library.common.entity.AccountData
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentMeBinding
import com.lee.playandroid.router.navigateLogin

/**
 * @author jv.lee
 * @data 2021/11/2
 * @description 首页tab 我的页面
 */
class MeFragment : BaseFragment(R.layout.fragment_me), View.OnClickListener {

    private val binding by binding(FragmentMeBinding::bind)

    private val accountService = ModuleService.find<AccountService>()

    override fun bindView() {
        delayBackEvent()

        binding.toolbarLayout.setOnClickListener(this)
        binding.lineSettings.setOnClickListener(this)
        binding.lineIntegral.setOnClickListener(this)
        binding.lineCollect.setOnClickListener(this)
        binding.lineShare.setOnClickListener(this)
        binding.lineTodo.setOnClickListener(this)
    }

    override fun bindData() {
        accountService.getAccountLive(requireActivity())
            .observeState<AccountData>(this, success = {
                setLoginAccountUi(it)
            }, default = {
                setUnLoginAccountUi()
            }, error = {
                setUnLoginAccountUi()
            })
    }

    override fun onClick(view: View) {
        //无需校验登陆状态
        if (view == binding.lineSettings) {
            findNavController().navigate(R.id.action_me_fragment_to_settings_fragment)
            return
        }

        //需要校验登陆状态
        if (accountService.isLogin()) {
            when (view) {
                binding.lineIntegral ->
                    findNavController().navigate(R.id.action_me_fragment_to_integral_fragment)
                binding.lineCollect ->
                    findNavController().navigate(R.id.action_me_fragment_to_collect_fragment)
                binding.lineShare ->
                    findNavController().navigate(R.id.action_me_fragment_to_share_fragment)
                binding.lineTodo ->
                    findNavController().navigate(R.id.action_me_fragment_to_todo_fragment)
            }
        } else {
            toast(getString(R.string.me_login_message))
            findNavController().navigateLogin()
        }
    }

    /**
     * 设置登陆状态ui
     * @param account 账户信息
     */
    private fun setLoginAccountUi(account: AccountData) {

    }

    /**
     * 设置未登状态ui
     */
    private fun setUnLoginAccountUi() {

    }

}