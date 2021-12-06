package com.lee.playandroid.me.ui.fragment

import android.view.View
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseVMFragment
import com.lee.library.extensions.delayBackEvent
import com.lee.library.extensions.setBackgroundColorCompat
import com.lee.library.extensions.setTextColorCompat
import com.lee.library.extensions.toast
import com.lee.library.mvvm.base.BaseViewModel
import com.lee.library.mvvm.ui.observeState
import com.lee.library.tools.DarkViewUpdateTools
import com.lee.playandroid.library.common.entity.AccountData
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentMeBinding
import com.lee.playandroid.router.navigateLogin

/**
 * @author jv.lee
 * @date 2021/11/2
 * @description 首页tab 我的页面
 */
class MeFragment : BaseVMFragment<FragmentMeBinding, BaseViewModel>(R.layout.fragment_me),
    View.OnClickListener, DarkViewUpdateTools.ViewCallback {

    private val accountService = ModuleService.find<AccountService>()

    override fun bindView() {
        delayBackEvent()
        DarkViewUpdateTools.bindViewCallback(this, this)

        binding.onClickListener = this
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
                    findNavController().navigate(R.id.action_me_fragment_to_coin_fragment)
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

    override fun updateDarkView() {
        binding.constRoot.setBackgroundColorCompat(R.color.colorThemeBackground)
        binding.toolbarLayout.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.tvAccountName.setTextColorCompat(R.color.colorThemeAccent)

        binding.lineIntegral.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.lineIntegral.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)

        binding.lineCollect.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.lineCollect.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)

        binding.lineShare.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.lineShare.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)

        binding.lineTodo.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.lineTodo.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)

        binding.lineSettings.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.lineSettings.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)
    }

    /**
     * 设置登陆状态ui
     * @param account 账户信息
     */
    private fun setLoginAccountUi(account: AccountData) {
        binding.ivHeader.setImageResource(R.mipmap.ic_launcher_round)
        binding.tvAccountName.text = account.userInfo.username
        binding.tvLevel.text =
            getString(R.string.me_account_info_text, account.coinInfo.level, account.coinInfo.rank)
        binding.tvLevel.visibility = View.VISIBLE
    }

    /**
     * 设置未登状态ui
     */
    private fun setUnLoginAccountUi() {
        binding.ivHeader.setImageResource(R.drawable.vector_account)
        binding.tvAccountName.text = getString(R.string.me_account_default_text)
        binding.tvLevel.text = ""
        binding.tvLevel.visibility = View.GONE
    }

}