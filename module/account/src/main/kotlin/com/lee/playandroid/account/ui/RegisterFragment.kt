package com.lee.playandroid.account.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
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
import com.lee.playandroid.account.constants.Constants
import com.lee.playandroid.account.databinding.FragmentRegisterBinding
import com.lee.playandroid.account.viewmodel.AccountViewModel
import com.lee.playandroid.account.viewmodel.LoginRegisterViewModel
import com.lee.playandroid.library.common.constants.Constants.REQUEST_KEY
import com.lee.playandroid.library.common.entity.AccountData

/**
 * @author jv.lee
 * @date 2021/11/24
 * @description 注册页面
 */
class RegisterFragment : BaseFragment(R.layout.fragment_register), View.OnClickListener,
    TextWatcherAdapter {

    private val viewModel by viewModels<LoginRegisterViewModel>()
    private val accountViewModel by activityViewModels<AccountViewModel>()

    private val binding by binding(FragmentRegisterBinding::bind)

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun bindView() {
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
        binding.editRePassword.addTextChangedListener(this)
    }

    override fun bindData() {
        //监听注册成功后获取的账户信息
        viewModel.accountLive.observeState<AccountData>(viewLifecycleOwner, success = {
            dismiss(loadingDialog)
            PreferencesTools.put(Constants.KEY_SAVE_INPUT_USERNAME, it.userInfo.username)
            accountViewModel.updateAccountInfo(it)
            setFragmentResult(REQUEST_KEY, Bundle.EMPTY)
            findNavController().popBackStack()
        }, error = {
            dismiss(loadingDialog)
            toast(it.message)
        })
    }

    override fun onClick(view: View) {
        when (view) {
            binding.tvRegister -> {
                requestRegister()
            }
            binding.tvLogin -> {
                goLogin()
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (binding.editUsername.text.toString().isEmpty() ||
            binding.editPassword.text.toString().isEmpty() ||
            binding.editRePassword.text.toString().isEmpty()
        ) {
            binding.tvRegister.setButtonDisable(true)
        } else {
            binding.tvRegister.setButtonDisable(false)
        }
    }

    /**
     * 回退至登陆页
     * 判断当前软键盘是否弹起，优先关闭软键盘
     */
    private fun goLogin() {
        if (binding.rootLayout.paddingBottom > 10) {
            KeyboardTools.hideSoftInput(requireActivity())
        } else {
            findNavController().popBackStack()
        }
    }

    /**
     * 发起注册处理
     * 隐藏键盘后延时处理使ui更平滑
     */
    private fun requestRegister() {
        KeyboardTools.hideSoftInput(requireActivity())
        binding.tvRegister.postDelayed({
            show(loadingDialog)
            viewModel.requestRegister(
                binding.editUsername.text.toString(),
                binding.editPassword.text.toString(),
                binding.editRePassword.text.toString()
            )
        }, 300)
    }

}