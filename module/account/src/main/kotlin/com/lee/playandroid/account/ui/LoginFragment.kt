package com.lee.playandroid.account.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
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
import com.lee.playandroid.account.databinding.FragmentLoginBinding
import com.lee.playandroid.account.viewmodel.*
import com.lee.playandroid.library.common.extensions.actionFailed
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/11/24
 * @description 登陆页面
 */
class LoginFragment : BaseNavigationFragment(R.layout.fragment_login), View.OnClickListener {

    companion object {
        // login/register 页面回传注册key
        const val REQUEST_KEY_LOGIN = "requestKey:login"
    }

    private val viewModel by viewModels<LoginViewModel>()
    private val accountViewModel by activityViewModels<AccountViewModel>()

    private val binding by binding(FragmentLoginBinding::bind)

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun bindView() {
        // 设置点击空白区域隐藏软键盘
        binding.root.parentTouchHideSoftInput()

        // 监听键盘弹起
        binding.root.keyboardPaddingBottom()

        // 设置监听
        binding.tvLogin.setOnClickListener(this)
        binding.tvRegister.setOnClickListener(this)
        binding.editUsername.addTextChangedListener(object : TextWatcherAdapter {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.dispatch(LoginViewAction.ChangeUsername(s?.toString() ?: ""))
            }
        })
        binding.editPassword.addTextChangedListener(object : TextWatcherAdapter {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.dispatch(LoginViewAction.ChangePassword(s?.toString() ?: ""))
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
                        if (requireContext().keyboardIsShow()) requireContext().hideSoftInput()
                        else findNavController().navigate(R.id.action_login_fragment_to_register_fragment)
                    }
                }
            }
        }

        viewModel.viewStates.run {
            launchWhenResumed {
                collectState(LoginViewState::hideKeyboard) {
                    if (it) requireContext().hideSoftInput()
                }
            }
            launchWhenResumed {
                collectState(LoginViewState::isLoading) {
                    if (it) show(loadingDialog) else dismiss(loadingDialog)
                }
            }
            launchWhenResumed {
                collectState(LoginViewState::isLoginEnable) {
                    binding.tvLogin.setButtonDisable(!it)
                }
            }
            launchWhenResumed {
                collectState(LoginViewState::username) {
                    binding.editUsername.setText(it)
                    binding.editUsername.setSelection(it.length)
                }
            }
            launchWhenResumed {
                collectState(LoginViewState::password) {
                    binding.editPassword.setText(it)
                    binding.editPassword.setSelection(it.length)
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.tvRegister -> viewModel.dispatch(LoginViewAction.NavigationRegister)
            binding.tvLogin -> viewModel.dispatch(LoginViewAction.RequestLogin)
        }
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        viewModel.dispatch(LoginViewAction.HideKeyboard)
    }

}