package com.lee.playandroid.account.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.account.databinding.FragmentRegisterBinding
import com.lee.playandroid.account.ui.LoginFragment.Companion.REQUEST_KEY_LOGIN
import com.lee.playandroid.account.viewmodel.AccountViewModel
import com.lee.playandroid.account.viewmodel.RegisterViewEvent
import com.lee.playandroid.account.viewmodel.RegisterViewIntent
import com.lee.playandroid.account.viewmodel.RegisterViewModel
import com.lee.playandroid.account.viewmodel.RegisterViewState
import com.lee.playandroid.base.base.BaseBindingNavigationFragment
import com.lee.playandroid.base.dialog.LoadingDialog
import com.lee.playandroid.base.extensions.collectState
import com.lee.playandroid.base.extensions.dismiss
import com.lee.playandroid.base.extensions.show
import com.lee.playandroid.base.interadp.TextWatcherAdapter
import com.lee.playandroid.base.tools.SystemBarTools.hasSoftInputShow
import com.lee.playandroid.base.tools.SystemBarTools.hideSoftInput
import com.lee.playandroid.base.tools.SystemBarTools.parentTouchHideSoftInput
import com.lee.playandroid.base.tools.SystemBarTools.softInputBottomPaddingChange
import com.lee.playandroid.common.extensions.actionFailed

/**
 * 注册页面
 * @author jv.lee
 * @date 2021/11/24
 */
class RegisterFragment : BaseBindingNavigationFragment<FragmentRegisterBinding>(), View.OnClickListener {

    private val viewModel by viewModels<RegisterViewModel>()
    private val accountViewModel by activityViewModels<AccountViewModel>()

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun bindView() {
        // 设置点击空白区域隐藏软键盘
        requireActivity().window.parentTouchHideSoftInput()

        // 监听键盘弹起
        mBinding.root.softInputBottomPaddingChange()

        // 设置监听
        mBinding.tvLogin.setOnClickListener(this)
        mBinding.tvRegister.setOnClickListener(this)
        mBinding.editUsername.addTextChangedListener(object : TextWatcherAdapter {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.dispatch(RegisterViewIntent.ChangeUsername(s?.toString() ?: ""))
            }
        })
        mBinding.editPassword.addTextChangedListener(object : TextWatcherAdapter {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.dispatch(RegisterViewIntent.ChangePassword(s?.toString() ?: ""))
            }
        })
        mBinding.editRePassword.addTextChangedListener(object : TextWatcherAdapter {
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
                    mBinding.tvRegister.setButtonDisable(!it)
                }
            }
            launchWhenResumed {
                collectState(RegisterViewState::username) {
                    mBinding.editUsername.setText(it)
                    mBinding.editUsername.setSelection(it.length)
                }
            }
            launchWhenResumed {
                collectState(RegisterViewState::password) {
                    mBinding.editPassword.setText(it)
                    mBinding.editPassword.setSelection(it.length)
                }
            }
            launchWhenResumed {
                collectState(RegisterViewState::rePassword) {
                    mBinding.editRePassword.setText(it)
                    mBinding.editRePassword.setSelection(it.length)
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view) {
            mBinding.tvRegister -> viewModel.dispatch(RegisterViewIntent.RequestRegister)
            mBinding.tvLogin -> viewModel.dispatch(RegisterViewIntent.NavigationLogin)
        }
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        viewModel.dispatch(RegisterViewIntent.HideKeyboard)
    }
}