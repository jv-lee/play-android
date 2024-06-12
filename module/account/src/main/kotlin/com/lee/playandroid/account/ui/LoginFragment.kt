package com.lee.playandroid.account.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.playandroid.account.R
import com.lee.playandroid.account.databinding.FragmentLoginBinding
import com.lee.playandroid.account.viewmodel.AccountViewModel
import com.lee.playandroid.account.viewmodel.LoginViewEvent
import com.lee.playandroid.account.viewmodel.LoginViewIntent
import com.lee.playandroid.account.viewmodel.LoginViewModel
import com.lee.playandroid.account.viewmodel.LoginViewState
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
 * 登陆页面
 * @author jv.lee
 * @date 2021/11/24
 */
class LoginFragment : BaseBindingNavigationFragment<FragmentLoginBinding>(), View.OnClickListener {

    companion object {
        /** 注册界面注册成功后回调登陆页面回传key（通知登陆页面已注册成功） */
        const val REQUEST_KEY_LOGIN = "requestKey:login"
    }

    private val viewModel by viewModels<LoginViewModel>()
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
                viewModel.dispatch(LoginViewIntent.ChangeUsername(s?.toString() ?: ""))
            }
        })
        mBinding.editPassword.addTextChangedListener(object : TextWatcherAdapter {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.dispatch(LoginViewIntent.ChangePassword(s?.toString() ?: ""))
            }
        })
    }

    override fun LifecycleCoroutineScope.bindData() {
        // 监听登陆页面回调时是否已经成功注册 注册成功后直接退出至前置页面
        setFragmentResultListener(REQUEST_KEY_LOGIN) { _: String, _: Bundle ->
            findNavController().popBackStack()
        }

        launchWhenResumed {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is LoginViewEvent.LoginFailed -> {
                        actionFailed(event.error)
                    }
                    is LoginViewEvent.LoginSuccess -> {
                        accountViewModel.dispatch(event.status)
                        findNavController().popBackStack()
                    }
                    is LoginViewEvent.NavigationRegisterEvent -> {
                        requireActivity().window.run {
                            if (hasSoftInputShow()) hideSoftInput()
                            else findNavController().navigate(
                                R.id.action_login_fragment_to_register_fragment
                            )
                        }
                    }
                }
            }
        }

        viewModel.viewStates.run {
            launchWhenResumed {
                collectState(LoginViewState::hideKeyboard) {
                    if (it) requireActivity().window.hideSoftInput()
                }
            }
            launchWhenResumed {
                collectState(LoginViewState::isLoading) {
                    if (it) show(loadingDialog) else dismiss(loadingDialog)
                }
            }
            launchWhenResumed {
                collectState(LoginViewState::isLoginEnable) {
                    mBinding.tvLogin.setButtonDisable(!it)
                }
            }
            launchWhenResumed {
                collectState(LoginViewState::username) {
                    mBinding.editUsername.setText(it)
                    mBinding.editUsername.setSelection(it.length)
                }
            }
            launchWhenResumed {
                collectState(LoginViewState::password) {
                    mBinding.editPassword.setText(it)
                    mBinding.editPassword.setSelection(it.length)
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view) {
            mBinding.tvRegister -> viewModel.dispatch(LoginViewIntent.NavigationRegister)
            mBinding.tvLogin -> viewModel.dispatch(LoginViewIntent.RequestLogin)
        }
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        viewModel.dispatch(LoginViewIntent.HideKeyboard)
    }
}