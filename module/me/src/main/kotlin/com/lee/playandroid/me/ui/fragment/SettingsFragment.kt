package com.lee.playandroid.me.ui.fragment

import android.app.Dialog
import android.view.View
import android.widget.CompoundButton
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.base.base.BaseBindingNavigationFragment
import com.lee.playandroid.base.dialog.ChoiceDialog
import com.lee.playandroid.base.dialog.LoadingDialog
import com.lee.playandroid.base.extensions.collectState
import com.lee.playandroid.base.extensions.dismiss
import com.lee.playandroid.base.extensions.setBackgroundColorCompat
import com.lee.playandroid.base.extensions.setTextColorCompat
import com.lee.playandroid.base.extensions.show
import com.lee.playandroid.base.extensions.toast
import com.lee.playandroid.base.tools.DarkViewUpdateTools
import com.lee.playandroid.common.entity.AccountViewEvent
import com.lee.playandroid.common.entity.AccountViewState
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentSettingsBinding
import com.lee.playandroid.me.viewmodel.SettingsViewEvent
import com.lee.playandroid.me.viewmodel.SettingsViewIntent
import com.lee.playandroid.me.viewmodel.SettingsViewModel
import com.lee.playandroid.me.viewmodel.SettingsViewState
import com.lee.playandroid.me.viewmodel.ThemeViewIntent
import com.lee.playandroid.me.viewmodel.ThemeViewModel
import com.lee.playandroid.me.viewmodel.ThemeViewState
import kotlinx.coroutines.launch

/**
 * 应用设置页
 * @author jv.lee
 * @date 2021/11/25
 */
class SettingsFragment :
    BaseBindingNavigationFragment<FragmentSettingsBinding>(),
    View.OnClickListener,
    CompoundButton.OnCheckedChangeListener,
    DarkViewUpdateTools.ViewCallback {

    private val viewModel by viewModels<SettingsViewModel>()

    private val themeViewModel by activityViewModels<ThemeViewModel>()

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    private lateinit var clearDialog: Dialog
    private lateinit var logoutDialog: Dialog

    override fun bindView() {
        DarkViewUpdateTools.bindViewCallback(viewLifecycleOwner, this)

        mBinding.lineSystem.getRightSwitch()?.setOnCheckedChangeListener(this)
        mBinding.lineNight.getRightSwitch()?.setOnCheckedChangeListener(this)

        mBinding.lineClearCache.setOnClickListener(this)
        mBinding.lineLogout.setOnClickListener(this)

        createAlertDialog()
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchWhenResumed {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is SettingsViewEvent.ClearCacheResult -> {
                        toast(event.message)
                    }
                }
            }
        }

        launchWhenResumed {
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
            launchWhenResumed {
                collectState(AccountViewState::isLoading) {
                    if (it) show(loadingDialog) else dismiss(loadingDialog)
                }
            }
            launchWhenResumed {
                collectState(AccountViewState::isLogin) {
                    mBinding.lineLogout.visibility = if (it) View.VISIBLE else View.GONE
                }
            }
        }

        viewModel.viewStates.run {
            launchWhenResumed {
                collectState(SettingsViewState::isLoading) {
                    if (it) show(loadingDialog) else dismiss(loadingDialog)
                }
            }
            launchWhenResumed {
                collectState(SettingsViewState::isCacheConfirm) {
                    if (it) show(clearDialog) else dismiss(clearDialog)
                }
            }
            launchWhenResumed {
                collectState(SettingsViewState::isLogoutConfirm) {
                    if (it) show(logoutDialog) else dismiss(logoutDialog)
                }
            }
            launchWhenResumed {
                collectState(SettingsViewState::totalCacheSize) {
                    mBinding.lineClearCache.getRightTextView().text = it
                }
            }
        }

        themeViewModel.viewStates.run {
            launchWhenResumed {
                collectState(ThemeViewState::isSystem, ThemeViewState::isDark) { isSystem, isDark ->
                    mBinding.lineSystem.getRightSwitch()?.isChecked = isSystem
                    mBinding.lineNight.getRightSwitch()?.isChecked = isDark
                    mBinding.lineNight.getRightSwitch()?.isEnabled = !isSystem
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v) {
            mBinding.lineLogout -> {
                viewModel.dispatch(SettingsViewIntent.VisibleLogoutDialog(visibility = true))
            }
            mBinding.lineClearCache -> {
                viewModel.dispatch(SettingsViewIntent.VisibleCacheDialog(visibility = true))
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (!isResumed) return

        when (buttonView) {
            mBinding.lineSystem.getRightSwitch() -> {
                themeViewModel.dispatch(ThemeViewIntent.UpdateSystemStatus(isChecked))
            }
            mBinding.lineNight.getRightSwitch() -> {
                themeViewModel.dispatch(ThemeViewIntent.UpdateDarkStatus(isChecked))
            }
        }
    }

    override fun updateDarkView() {
        mBinding.constRoot.setBackgroundColorCompat(R.color.colorThemeBackground)
        mBinding.toolbar.setBackgroundColorCompat(R.color.colorThemeItem)
        mBinding.toolbar.setTitleColor(R.color.colorThemeAccent)
        mBinding.toolbar.setBackDrawableRes(R.drawable.vector_back, R.color.colorThemeAccent)

        mBinding.lineClearCache.setBackgroundColorCompat(R.color.colorThemeItem)
        mBinding.lineClearCache.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)
        mBinding.lineClearCache.getRightTextView().setTextColorCompat(R.color.colorThemeAccent)

        mBinding.lineLogout.setBackgroundColorCompat(R.color.colorThemeItem)
        mBinding.lineLogout.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)

        mBinding.lineSystem.setBackgroundColorCompat(R.color.colorThemeItem)
        mBinding.lineSystem.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)

        mBinding.lineNight.setBackgroundColorCompat(R.color.colorThemeItem)
        mBinding.lineNight.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)

        createAlertDialog()
    }

    /**
     * 创建设置页Dialog提示弹窗
     */
    private fun createAlertDialog() {
        // 清除弹窗创建
        clearDialog = ChoiceDialog(requireContext()).apply {
            setTitle(getString(R.string.settings_clear_title))
            setCancelable(true)
            onCancel = {
                viewModel.dispatch(SettingsViewIntent.VisibleCacheDialog(visibility = false))
            }
            onConfirm = {
                viewModel.dispatch(SettingsViewIntent.RequestClearCache)
            }
        }

        // 退出登陆弹窗创建
        logoutDialog = ChoiceDialog(requireContext()).apply {
            setTitle(getString(R.string.settings_logout_title))
            setCancelable(true)
            onCancel = {
                viewModel.dispatch(SettingsViewIntent.VisibleLogoutDialog(visibility = false))
            }
            onConfirm = {
                viewModel.viewModelScope.launch {
                    viewModel.accountService.requestLogout(requireActivity())
                    viewModel.dispatch(SettingsViewIntent.VisibleLogoutDialog(visibility = false))
                }
            }
        }
    }
}