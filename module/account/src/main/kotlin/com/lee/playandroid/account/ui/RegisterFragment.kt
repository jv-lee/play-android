package com.lee.playandroid.account.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.dialog.LoadingDialog
import com.lee.library.extensions.binding
import com.lee.library.extensions.dismiss
import com.lee.library.extensions.show
import com.lee.library.interadp.TextWatcherAdapter
import com.lee.library.tools.KeyboardTools.hideSoftInput
import com.lee.library.tools.KeyboardTools.keyboardIsShow
import com.lee.library.tools.KeyboardTools.keyboardPaddingBottom
import com.lee.library.tools.KeyboardTools.parentTouchHideSoftInput
import com.lee.library.viewstate.collectState
import com.lee.playandroid.account.R
import com.lee.playandroid.account.databinding.FragmentRegisterBinding
import com.lee.playandroid.account.ui.LoginFragment.Companion.REQUEST_KEY_LOGIN
import com.lee.playandroid.account.viewmodel.*
import com.lee.playandroid.library.common.entity.AccountViewAction
import com.lee.playandroid.library.common.extensions.actionFailed
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/11/24
 * @description 注册页面
 */
class RegisterFragment : BaseNavigationFragment(R.layout.fragment_register), View.OnClickListener {

    private val viewModel by viewModels<RegisterViewModel>()
    private val accountViewModel by activityViewModels<AccountViewModel>()

    private val binding by binding(FragmentRegisterBinding::bind)

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun bindView() {
        // 设置点击空白区域隐藏软键盘
        requireContext().parentTouchHideSoftInput(binding.root)

        // 监听键盘弹起
        requireActivity().window.decorView.keyboardPaddingBottom(viewLifecycleOwner)

        // 设置监听
        binding.root.setOnClickListener(this)
        binding.tvLogin.setOnClickListener(this)
        binding.tvRegister.setOnClickListener(this)
        binding.editUsername.addTextChangedListener(object : TextWatcherAdapter {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.dispatch(RegisterViewAction.ChangeUsername(s?.toString() ?: ""))
            }
        })
        binding.editPassword.addTextChangedListener(object : TextWatcherAdapter {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.dispatch(RegisterViewAction.ChangePassword(s?.toString() ?: ""))
            }
        })
        binding.editRePassword.addTextChangedListener(object : TextWatcherAdapter {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.dispatch(RegisterViewAction.ChangeRePassword(s?.toString() ?: ""))
            }
        })
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchWhenResumed {
            // 监听注册成功后获取的账户信息
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is RegisterViewEvent.RegisterSuccess -> {
                        accountViewModel.dispatch(
                            AccountViewAction.UpdateAccountStatus(event.accountData, true)
                        )
                        setFragmentResult(REQUEST_KEY_LOGIN, Bundle.EMPTY)
                        findNavController().popBackStack()
                    }
                    is RegisterViewEvent.RegisterFailed -> {
                        actionFailed(event.error)
                    }
                }
            }
        }

        viewModel.viewStates.run {
            launchWhenResumed {
                collectState(RegisterViewState::isLoading) {
                    if (it) show(loadingDialog) else dismiss(loadingDialog)
                }
            }
            launchWhenResumed {
                collectState(RegisterViewState::isRegisterEnable) {
                    binding.tvRegister.setButtonDisable(!it)
                }
            }
            launchWhenResumed {
                collectState(RegisterViewState::username) {
                    binding.editUsername.setText(it)
                    binding.editUsername.setSelection(it.length)
                }
            }
            launchWhenResumed {
                collectState(RegisterViewState::password) {
                    binding.editPassword.setText(it)
                    binding.editPassword.setSelection(it.length)
                }
            }
            launchWhenResumed {
                collectState(RegisterViewState::rePassword) {
                    binding.editRePassword.setText(it)
                    binding.editRePassword.setSelection(it.length)
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.tvRegister -> requestRegister()
            binding.tvLogin -> goLogin()
            binding.root -> requireActivity().hideSoftInput()
        }
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        requireContext().hideSoftInput()
    }

    /**
     * 回退至登陆页
     * 判断当前软键盘是否弹起，优先关闭软键盘
     */
    private fun goLogin() {
        if (requireContext().keyboardIsShow()) {
            requireActivity().hideSoftInput()
        } else {
            findNavController().popBackStack()
        }
    }

    /**
     * 发起注册处理
     * 隐藏键盘后延时处理使ui更平滑
     */
    private fun requestRegister() {
        requireActivity().hideSoftInput()
        binding.tvRegister.postDelayed({
            viewModel.dispatch(RegisterViewAction.RequestRegister)
        }, 300)
    }

}