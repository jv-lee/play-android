package com.lee.playandroid.account.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.account.R
import com.lee.playandroid.account.databinding.FragmentRegisterBinding
import com.lee.playandroid.account.ui.LoginFragment.Companion.REQUEST_KEY_LOGIN
import com.lee.playandroid.account.viewmodel.*
import com.lee.playandroid.base.base.BaseNavigationFragment
import com.lee.playandroid.base.dialog.LoadingDialog
import com.lee.playandroid.base.extensions.binding
import com.lee.playandroid.base.extensions.dismiss
import com.lee.playandroid.base.extensions.show
import com.lee.playandroid.base.interadp.TextWatcherAdapter
import com.lee.playandroid.base.tools.SystemBarTools.hasSoftInputShow
import com.lee.playandroid.base.tools.SystemBarTools.hideSoftInput
import com.lee.playandroid.base.tools.SystemBarTools.parentTouchHideSoftInput
import com.lee.playandroid.base.tools.SystemBarTools.softInputBottomPaddingChange
import com.lee.playandroid.base.extensions.collectState
import com.lee.playandroid.common.extensions.actionFailed

/**
 * 注册页面
 * @author jv.lee
 * @date 2021/11/24
 */
class RegisterFragment : BaseNavigationFragment(R.layout.fragment_register), View.OnClickListener {

    private val viewModel by viewModels<RegisterViewModel>()
    private val accountViewModel by activityViewModels<AccountViewModel>()

    private val binding by binding(FragmentRegisterBinding::bind)

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun bindView() {
        // 设置点击空白区域隐藏软键盘
        requireActivity().window.parentTouchHideSoftInput()

        // 监听键盘弹起
        binding.root.softInputBottomPaddingChange()

        // 设置监听
        binding.tvLogin.setOnClickListener(this)
        binding.tvRegister.setOnClickListener(this)
        binding.editUsername.addTextChangedListener(object : TextWatcherAdapter {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.dispatch(RegisterViewIntent.ChangeUsername(s?.toString() ?: ""))
            }
        })
        binding.editPassword.addTextChangedListener(object : TextWatcherAdapter {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.dispatch(RegisterViewIntent.ChangePassword(s?.toString() ?: ""))
            }
        })
        binding.editRePassword.addTextChangedListener(object : TextWatcherAdapter {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.dispatch(RegisterViewIntent.ChangeRePassword(s?.toString() ?: ""))
            }
        })
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchWhenResumed {
            // 监听注册成功后获取的账户信息
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is RegisterViewEvent.RegisterFailed -> {
                        actionFailed(event.error)
                    }
                    is RegisterViewEvent.RegisterSuccess -> {
                        accountViewModel.dispatch(event.status)
                        setFragmentResult(REQUEST_KEY_LOGIN, Bundle.EMPTY)
                        findNavController().popBackStack()
                    }
                    is RegisterViewEvent.NavigationLoginEvent -> {
                        requireActivity().window.run {
                            if (hasSoftInputShow()) hideSoftInput()
                            else findNavController().popBackStack()
                        }
                    }
                }
            }
        }

        viewModel.viewStates.run {
            launchWhenResumed {
                collectState(RegisterViewState::hideKeyboard) {
                    if (it) requireActivity().window.hideSoftInput()
                }
            }
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
            binding.tvRegister -> viewModel.dispatch(RegisterViewIntent.RequestRegister)
            binding.tvLogin -> viewModel.dispatch(RegisterViewIntent.NavigationLogin)
        }
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        viewModel.dispatch(RegisterViewIntent.HideKeyboard)
    }
}