package com.lee.playandroid.me.ui.fragment

import android.view.View
import android.widget.CompoundButton
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseVMFragment
import com.lee.library.dialog.LoadingDialog
import com.lee.library.extensions.*
import com.lee.library.mvvm.ui.observeState
import com.lee.library.tools.DarkModeTools
import com.lee.library.tools.DarkViewUpdateTools
import com.lee.playandroid.library.common.entity.AccountData
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentMeBinding
import com.lee.playandroid.me.viewmodel.MeViewModel
import com.lee.playandroid.router.navigateLogin

/**
 * @author jv.lee
 * @date 2021/11/2
 * @description 首页tab 我的页面
 */
class MeFragment : BaseVMFragment<FragmentMeBinding, MeViewModel>(R.layout.fragment_me),
    View.OnClickListener, CompoundButton.OnCheckedChangeListener, DarkViewUpdateTools.ViewCallback {

    private val accountService = ModuleService.find<AccountService>()

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun bindView() {
        delayBackEvent()
        DarkViewUpdateTools.bindViewCallback(this, this)

        binding.switchSystemEnable.isChecked = DarkModeTools.get().isSystemTheme()
        binding.switchDarkEnable.isChecked = DarkModeTools.get().isDarkTheme()
        binding.switchDarkEnable.isEnabled = !DarkModeTools.get().isSystemTheme()

        binding.onClickListener = this
        binding.onCheckedChangeListener = this
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
        } else if (view == binding.lineLogout) {
            accountService.requestLogout(
                requireActivity(),
                { show(loadingDialog) },
                { dismiss(loadingDialog) },
                { toast(it) })
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

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (!isResumed) return

        when (buttonView) {
            binding.switchSystemEnable -> {
                DarkModeTools.get().updateSystemTheme(isChecked)
                binding.switchDarkEnable.isChecked = DarkModeTools.get().isDarkTheme()
                binding.switchDarkEnable.isEnabled = !isChecked
                DarkViewUpdateTools.notifyUiMode()
            }
            binding.switchDarkEnable -> {
                if (DarkModeTools.get().isDarkTheme() != isChecked) {
                    DarkModeTools.get().updateNightTheme(isChecked)
                    DarkViewUpdateTools.notifyUiMode()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.switchDarkEnable.setOnCheckedChangeListener(null)
        binding.switchSystemEnable.setOnCheckedChangeListener(null)
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

        binding.lineLogout.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.lineLogout.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)

        binding.lineSystem.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.lineSystem.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)

        binding.lineNight.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.lineNight.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)
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
        binding.lineLogout.visibility = View.VISIBLE
    }

    /**
     * 设置未登状态ui
     */
    private fun setUnLoginAccountUi() {
        binding.ivHeader.setImageResource(R.drawable.vector_account)
        binding.tvAccountName.text = getString(R.string.me_account_default_text)
        binding.tvLevel.text = ""
        binding.tvLevel.visibility = View.GONE
        binding.lineLogout.visibility = View.GONE
    }

}