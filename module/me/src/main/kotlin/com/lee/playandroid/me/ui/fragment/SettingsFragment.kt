package com.lee.playandroid.me.ui.fragment

import android.app.Dialog
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.dialog.ChoiceDialog
import com.lee.library.dialog.LoadingDialog
import com.lee.library.dialog.core.CancelListener
import com.lee.library.dialog.core.ConfirmListener
import com.lee.library.extensions.*
import com.lee.library.tools.DarkModeTools
import com.lee.library.tools.DarkViewUpdateTools
import com.lee.library.viewstate.collectState
import com.lee.playandroid.library.common.entity.AccountViewEvent
import com.lee.playandroid.library.common.entity.AccountViewState
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentSettingsBinding
import com.lee.playandroid.me.viewmodel.SettingsViewAction
import com.lee.playandroid.me.viewmodel.SettingsViewEvent
import com.lee.playandroid.me.viewmodel.SettingsViewModel
import com.lee.playandroid.me.viewmodel.SettingsViewState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description 应用设置页
 */
class SettingsFragment : BaseNavigationFragment(R.layout.fragment_settings),
    View.OnClickListener, CompoundButton.OnCheckedChangeListener, DarkViewUpdateTools.ViewCallback {

    private val viewModel by viewModels<SettingsViewModel>()

    private val binding by binding(FragmentSettingsBinding::bind)

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    private lateinit var clearDialog: Dialog
    private lateinit var logoutDialog: Dialog

    override fun bindView() {
        DarkViewUpdateTools.bindViewCallback(viewLifecycleOwner, this)

        binding.lineSystem.getRightSwitch()?.isChecked = DarkModeTools.get().isSystemTheme()
        binding.lineNight.getRightSwitch()?.isChecked = DarkModeTools.get().isDarkTheme()
        binding.lineNight.getRightSwitch()?.isEnabled = !DarkModeTools.get().isSystemTheme()

        binding.lineSystem.getRightSwitch()?.setOnCheckedChangeListener(this)
        binding.lineNight.getRightSwitch()?.setOnCheckedChangeListener(this)

        binding.lineClearCache.setOnClickListener(this)
        binding.lineLogout.setOnClickListener(this)

        createAlertDialog()
    }

    override fun bindData() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is SettingsViewEvent.ClearCacheResult -> {
                        toast(event.message)
                    }
                }
            }
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.accountService.getAccountViewEvents(requireActivity()).collect { event ->
                when (event) {
                    is AccountViewEvent.LogoutSuccess -> {
                        toast(event.message)
                    }
                    is AccountViewEvent.LogoutFailed -> {
                        toast(event.message)
                    }
                }
            }
        }

        viewModel.accountService.getAccountViewStates(requireActivity()).run {
            launchAndRepeatWithViewLifecycle {
                collectState(AccountViewState::isLoading) {
                    if (it) show(loadingDialog) else dismiss(loadingDialog)
                }
            }
            launchAndRepeatWithViewLifecycle {
                collectState(AccountViewState::isLogin) {
                    binding.lineLogout.visibility = if (it) View.VISIBLE else View.GONE
                }
            }
        }

        viewModel.viewStates.run {
            launchAndRepeatWithViewLifecycle {
                collectState(SettingsViewState::isLoading) {
                    if (it) show(loadingDialog) else dismiss(loadingDialog)
                }
            }
            launchAndRepeatWithViewLifecycle {
                collectState(SettingsViewState::isCacheConfirm) {
                    Log.i("jv.lee", "response:isCacheConfirm:$it")
                    if (it) show(clearDialog) else dismiss(clearDialog)
                }
            }
            launchAndRepeatWithViewLifecycle {
                collectState(SettingsViewState::isLogoutConfirm) {
                    Log.i("jv.lee", "response:isLogoutConfirm:$it")
                    if (it) show(logoutDialog) else dismiss(logoutDialog)
                }
            }
            launchAndRepeatWithViewLifecycle {
                collectState(SettingsViewState::totalCacheSize) {
                    binding.lineClearCache.getRightTextView().text = it
                }
            }
        }

    }

    override fun onClick(v: View) {
        when (v) {
            binding.lineLogout -> {
                viewModel.dispatch(SettingsViewAction.VisibleLogoutDialog(visibility = true))
            }
            binding.lineClearCache -> {
                viewModel.dispatch(SettingsViewAction.VisibleCacheDialog(visibility = true))
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

        createAlertDialog()
    }

    /**
     * 创建设置页Dialog提示弹窗
     */
    private fun createAlertDialog() {
        //清除弹窗创建
        clearDialog = ChoiceDialog(requireContext()).apply {
            setTitle(getString(R.string.settings_clear_title))
            setCancelable(true)
            cancelListener = CancelListener {
                viewModel.dispatch(SettingsViewAction.VisibleCacheDialog(visibility = false))
            }
            confirmListener = ConfirmListener {
                viewModel.dispatch(SettingsViewAction.RequestClearCache)
            }
        }

        //退出登陆弹窗创建
        logoutDialog = ChoiceDialog(requireContext()).apply {
            setTitle(getString(R.string.settings_logout_title))
            setCancelable(true)
            cancelListener = CancelListener {
                viewModel.dispatch(SettingsViewAction.VisibleLogoutDialog(visibility = false))
            }
            confirmListener = ConfirmListener {
                viewModel.viewModelScope.launch {
                    viewModel.accountService.requestLogout(requireActivity())
                    viewModel.dispatch(SettingsViewAction.VisibleLogoutDialog(visibility = false))
                }
            }
        }
    }

}