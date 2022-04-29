package com.lee.playandroid.account.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.dialog.LoadingDialog
import com.lee.library.extensions.binding
import com.lee.library.extensions.dismiss
import com.lee.library.extensions.launchAndRepeatWithViewLifecycle
import com.lee.library.extensions.show
import com.lee.library.interadp.TextWatcherAdapter
import com.lee.library.tools.KeyboardTools.hideSoftInput
import com.lee.library.tools.KeyboardTools.keyboardIsShow
import com.lee.library.tools.KeyboardTools.keyboardPaddingBottom
import com.lee.library.tools.KeyboardTools.parentTouchHideSoftInput
import com.lee.library.tools.PreferencesTools
import com.lee.library.viewstate.collectState
import com.lee.playandroid.account.R
import com.lee.playandroid.account.constants.Constants.SP_KEY_SAVE_INPUT_USERNAME
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
        requireContext().parentTouchHideSoftInput(binding.root)

        // 设置登陆过的账户名
        binding.editUsername.setText(PreferencesTools.get<String>(SP_KEY_SAVE_INPUT_USERNAME))

        // 监听键盘弹起
        requireActivity().window.decorView.keyboardPaddingBottom(viewLifecycleOwner)

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

    override fun bindData() {
        // 监听登陆页面回调时是否已经成功注册 注册成功后直接退出至前置页面
        setFragmentResultListener(REQUEST_KEY_LOGIN) { _: String, _: Bundle ->
            findNavController().popBackStack()
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is LoginViewEvent.LoginSuccess -> {
                        accountViewModel.updateAccountInfo(event.accountData)
                        findNavController().popBackStack()
                    }
                    is LoginViewEvent.LoginFailed -> {
                        actionFailed(event.error)
                    }
                }
            }
        }

        viewModel.viewStates.run {
            launchAndRepeatWithViewLifecycle {
                collectState(LoginViewState::isLoading) {
                    if (it) show(loadingDialog) else dismiss(loadingDialog)
                }
            }
            launchAndRepeatWithViewLifecycle {
                collectState(LoginViewState::isLoginEnable) {
                    binding.tvLogin.setButtonDisable(!it)
                }
            }
            launchAndRepeatWithViewLifecycle {
                collectState(LoginViewState::username) {
                    binding.editUsername.setText(it)
                    binding.editUsername.setSelection(it.length)
                }
            }
            launchAndRepeatWithViewLifecycle {
                collectState(LoginViewState::password) {
                    binding.editPassword.setText(it)
                    binding.editPassword.setSelection(it.length)
                }
            }
        }
    }

    override fun onFragmentStop() {
        super.onFragmentStop()
        requireContext().hideSoftInput()
    }

    override fun onClick(view: View) {
        when (view) {
            binding.tvRegister -> {
                goRegister()
            }
            binding.tvLogin -> {
                requestLogin()
            }
        }
    }

    /**
     * 跳转至注册页
     * 判断当前软键盘是否弹起，优先关闭软键盘
     */
    private fun goRegister() {
        if (requireContext().keyboardIsShow()) {
            requireActivity().hideSoftInput()
        } else {
            findNavController().navigate(R.id.action_login_fragment_to_register_fragment)
        }
    }

    /**
     * 发起登陆处理
     * 隐藏键盘后延时处理使ui更平滑
     */
    private fun requestLogin() {
        requireActivity().hideSoftInput()
        binding.tvLogin.postDelayed({
            viewModel.dispatch(LoginViewAction.RequestLogin)
        }, 300)
    }

}