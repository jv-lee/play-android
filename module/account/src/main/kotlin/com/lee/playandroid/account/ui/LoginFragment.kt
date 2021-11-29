package com.lee.playandroid.account.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseFragment
import com.lee.library.dialog.LoadingDialog
import com.lee.library.extensions.*
import com.lee.library.interadp.TextWatcherAdapter
import com.lee.library.mvvm.ui.observeState
import com.lee.library.tools.KeyboardTools
import com.lee.library.tools.PreferencesTools
import com.lee.playandroid.account.R
import com.lee.playandroid.account.constants.Constants.KEY_SAVE_INPUT_USERNAME
import com.lee.playandroid.account.databinding.FragmentLoginBinding
import com.lee.playandroid.account.viewmodel.AccountViewModel
import com.lee.playandroid.account.viewmodel.LoginRegisterViewModel
import com.lee.playandroid.library.common.constants.Constants.REQUEST_KEY
import com.lee.playandroid.library.common.entity.AccountData

/**
 * @author jv.lee
 * @date 2021/11/24
 * @description 登陆页面
 */
class LoginFragment : BaseFragment(R.layout.fragment_login), View.OnClickListener,
    TextWatcherAdapter {

    private val viewModel by viewModels<LoginRegisterViewModel>()
    private val accountViewModel by activityViewModels<AccountViewModel>()

    private val binding by binding(FragmentLoginBinding::bind)

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun bindView() {
        //设置登陆过的账户名
        binding.editUsername.setText(PreferencesTools.get(KEY_SAVE_INPUT_USERNAME, ""))

        //监听键盘弹起
        binding.rootLayout.keyboardObserver { diff ->
            if (isResumed) {
                binding.rootLayout.updatePadding(bottom = diff)
            }
        }

        //设置监听
        binding.tvLogin.setOnClickListener(this)
        binding.tvRegister.setOnClickListener(this)
        binding.editUsername.addTextChangedListener(this)
        binding.editPassword.addTextChangedListener(this)
    }

    override fun bindData() {
        //监听登陆页面回调时是否已经成功注册 注册成功后直接退出至前置页面
        setFragmentResultListener(REQUEST_KEY) { _: String, _: Bundle ->
            findNavController().popBackStack()
        }

        //监听登陆成功后获取的账户信息
        viewModel.accountLive.observeState<AccountData>(viewLifecycleOwner, success = {
            dismiss(loadingDialog)
            PreferencesTools.put(KEY_SAVE_INPUT_USERNAME, it.userInfo.username)
            accountViewModel.updateAccountInfo(it)
            findNavController().popBackStack()
        }, error = {
            dismiss(loadingDialog)
            toast(it.message)
        })
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

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (binding.editUsername.text.toString().isEmpty() ||
            binding.editPassword.text.toString().isEmpty()
        ) {
            binding.tvLogin.setButtonDisable(true)
        } else {
            binding.tvLogin.setButtonDisable(false)
        }
    }

    /**
     * 跳转至注册页
     * 判断当前软键盘是否弹起，优先关闭软键盘
     */
    private fun goRegister() {
        if (binding.rootLayout.paddingBottom > 10) {
            KeyboardTools.hideSoftInput(requireActivity())
        } else {
            findNavController().navigate(R.id.action_login_fragment_to_register_fragment)
        }
    }

    /**
     * 发起登陆处理
     * 隐藏键盘后延时处理使ui更平滑
     */
    private fun requestLogin() {
        KeyboardTools.hideSoftInput(requireActivity())
        binding.tvLogin.postDelayed({
            show(loadingDialog)
            viewModel.requestLogin(
                binding.editUsername.text.toString(),
                binding.editPassword.text.toString()
            )
        }, 300)
    }

}