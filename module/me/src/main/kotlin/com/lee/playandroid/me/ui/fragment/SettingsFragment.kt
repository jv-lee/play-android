package com.lee.playandroid.me.ui.fragment

import android.app.Dialog
import android.view.View
import android.widget.CompoundButton
import com.lee.library.base.BaseFragment
import com.lee.library.dialog.ChoiceDialog
import com.lee.library.dialog.LoadingDialog
import com.lee.library.dialog.core.ConfirmListener
import com.lee.library.extensions.*
import com.lee.library.tools.DarkModeTools
import com.lee.library.tools.DarkViewUpdateTools
import com.lee.library.utils.CacheUtil
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentSettingsBinding

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description 应用设置页
 */
class SettingsFragment : BaseFragment(R.layout.fragment_settings),
    View.OnClickListener, CompoundButton.OnCheckedChangeListener, DarkViewUpdateTools.ViewCallback {

    private val accountService = ModuleService.find<AccountService>()

    private val binding by binding(FragmentSettingsBinding::bind)

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    private lateinit var clearDialog: Dialog

    override fun bindView() {
        DarkViewUpdateTools.bindViewCallback(viewLifecycleOwner, this)

        binding.lineSystem.getRightSwitch()?.isChecked = DarkModeTools.get().isSystemTheme()
        binding.lineNight.getRightSwitch()?.isChecked = DarkModeTools.get().isDarkTheme()
        binding.lineNight.getRightSwitch()?.isEnabled = !DarkModeTools.get().isSystemTheme()

        binding.lineSystem.getRightSwitch()?.setOnCheckedChangeListener(this)
        binding.lineNight.getRightSwitch()?.setOnCheckedChangeListener(this)

        binding.lineClearCache.setOnClickListener(this)
        binding.lineLogout.setOnClickListener(this)

        createClearDialog()
        changeLogoutLine()
    }

    override fun bindData() {
        binding.lineClearCache.getRightTextView().text = CacheUtil.getTotalCacheSize(activity)
    }

    override fun onClick(v: View) {
        when (v) {
            binding.lineLogout -> {
                accountService.requestLogout(requireActivity(), {
                    show(loadingDialog)
                }, {
                    dismiss(loadingDialog)
                    changeLogoutLine()
                }, {
                    toast(it)
                })
            }
            binding.lineClearCache -> {
                show(clearDialog)
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (!isResumed) return

        when (buttonView) {
            binding.lineSystem.getRightSwitch() -> {
                DarkModeTools.get().updateSystemTheme(isChecked)
                DarkViewUpdateTools.notifyUiMode()
                //系统主题更新动态更新夜间模式状态
                binding.lineNight.getRightSwitch()?.isChecked = DarkModeTools.get().isDarkTheme()
                binding.lineNight.getRightSwitch()?.isEnabled = !isChecked
            }
            binding.lineNight.getRightSwitch() -> {
                if (DarkModeTools.get().isDarkTheme() != isChecked) {
                    DarkModeTools.get().updateNightTheme(isChecked)
                    DarkViewUpdateTools.notifyUiMode()
                }
            }
        }
    }

    override fun updateDarkView() {
        binding.constRoot.setBackgroundColorCompat(R.color.colorThemeBackground)
        binding.toolbar.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.toolbar.setTitleColor(R.color.colorThemeAccent)
        binding.toolbar.setBackDrawableRes(R.drawable.ic_back, R.color.colorThemeAccent)

        binding.lineClearCache.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.lineClearCache.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)
        binding.lineClearCache.getRightTextView().setTextColorCompat(R.color.colorThemeAccent)

        binding.lineLogout.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.lineLogout.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)

        binding.lineSystem.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.lineSystem.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)

        binding.lineNight.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.lineNight.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)

        createClearDialog()
    }

    /**
     * 获取登陆状态设置退出登陆item显示状态
     */
    private fun changeLogoutLine() {
        binding.lineLogout.visibility = if (accountService.isLogin()) View.VISIBLE else View.GONE
    }

    /**
     * 重建清除缓存弹窗
     */
    private fun createClearDialog() {
        clearDialog = ChoiceDialog(requireContext()).apply {
            setTitle(getString(R.string.settings_clear_title))
            setCancelable(true)
            confirmListener = ConfirmListener {
                if (CacheUtil.clearAllCache(activity)) {
                    binding.lineClearCache.getRightTextView().text =
                        CacheUtil.getTotalCacheSize(activity)
                    toast(getString(R.string.settings_clear_success))
                } else {
                    toast(getString(R.string.settings_clear_failed))
                }
                dismiss()
            }
        }
    }

}