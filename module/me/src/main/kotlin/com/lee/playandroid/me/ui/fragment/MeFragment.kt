package com.lee.playandroid.me.ui.fragment

import android.Manifest
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.imagetools.select.ImageLaunch
import com.imagetools.select.entity.SelectConfig
import com.imagetools.select.entity.TakeConfig
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.*
import com.lee.library.tools.DarkViewUpdateTools
import com.lee.library.tools.PermissionLauncher
import com.lee.library.viewstate.collectState
import com.lee.playandroid.library.common.entity.AccountData
import com.lee.playandroid.library.common.entity.AccountViewState
import com.lee.playandroid.me.BuildConfig
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentMeBinding
import com.lee.playandroid.me.viewmodel.MeViewModel
import com.lee.playandroid.router.navigateLogin
import com.lee.playandroid.router.navigateMyShare
import com.lee.playandroid.router.navigateTodo
import com.lee.playandroid.library.common.R as CR

/**
 * 首页第四个Tab 我的页面
 * @author jv.lee
 * @date 2021/11/2
 */
class MeFragment : BaseNavigationFragment(R.layout.fragment_me),
    View.OnClickListener, View.OnLongClickListener, DarkViewUpdateTools.ViewCallback {

    private val viewModel by viewModels<MeViewModel>()

    private val binding by binding(FragmentMeBinding::bind)

    private val imageLaunch = ImageLaunch(this)
    private val permissionLaunch = PermissionLauncher(this)

    override fun bindView() {
        // 拦截back处理
        delayBackEvent()
        // 监听当前深色主题变化
        DarkViewUpdateTools.bindViewCallback(viewLifecycleOwner, this)

        binding.toolbarLayout.setOnClickListener(this)
        binding.lineIntegral.setOnClickListener(this)
        binding.lineCollect.setOnClickListener(this)
        binding.lineShare.setOnClickListener(this)
        binding.lineTodo.setOnClickListener(this)
        binding.lineSettings.setOnClickListener(this)

        if (BuildConfig.DEBUG) {
            binding.lineIntegral.setOnLongClickListener(this)
            binding.lineCollect.setOnLongClickListener(this)
            binding.lineShare.setOnLongClickListener(this)
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchWhenResumed {
            viewModel.accountService.getAccountViewStates(requireActivity()).collectState(
                AccountViewState::isLogin,
                AccountViewState::accountData
            ) { isLogin, accountData ->
                if (isLogin) accountData?.let(this@MeFragment::setLoginAccountUi)
                else setUnLoginAccountUi()
            }
        }
    }

    override fun onClick(view: View) {
        // 无需校验登陆状态
        if (view == binding.lineSettings) {
            findNavController().navigate(R.id.action_me_fragment_to_settings_fragment)
            return
        }

        // 需要校验登陆状态
        if (viewModel.accountService.isLogin()) {
            when (view) {
                binding.lineIntegral ->
                    findNavController().navigate(R.id.action_me_fragment_to_coin_fragment)
                binding.lineCollect ->
                    findNavController().navigate(R.id.action_me_fragment_to_collect_fragment)
                binding.lineShare ->
                    findNavController().navigateMyShare()
                binding.lineTodo ->
                    findNavController().navigateTodo()
            }
        } else {
            toast(getString(CR.string.login_message))
            findNavController().navigateLogin()
        }
    }

    override fun onLongClick(v: View?): Boolean {
        when (v) {
            binding.lineIntegral -> {
                permissionLaunch.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, {
                    imageLaunch.select(
                        SelectConfig(isMultiple = false, isCompress = false, columnCount = 3)
                    ) {
                        toast(it[0].uri.toString())
                    }
                })
            }
            binding.lineCollect -> {
                permissionLaunch.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, {
                    imageLaunch.select(SelectConfig(isMultiple = true, isCompress = false)) {
                        toast(it[0].uri.toString())
                    }
                })
            }
            binding.lineShare -> {
                permissionLaunch.requestPermission(Manifest.permission.CAMERA, {
                    imageLaunch.take(TakeConfig(isCrop = true)) {
                        toast(it.uri.toString())
                    }
                })
            }
        }
        return true
    }

    override fun updateDarkView() {
        binding.constRoot.setBackgroundColorCompat(R.color.colorThemeBackground)
        binding.toolbarLayout.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.tvAccountName.setTextColorCompat(R.color.colorThemeAccent)
        binding.ivHeader.strokeColor =
            requireContext().getColorStateListCompat(R.color.colorThemeFocus)

        binding.lineIntegral.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.lineIntegral.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)

        binding.lineCollect.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.lineCollect.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)

        binding.lineShare.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.lineShare.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)

        binding.lineTodo.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.lineTodo.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)

        binding.lineSettings.setBackgroundColorCompat(R.color.colorThemeItem)
        binding.lineSettings.getLeftTextView().setTextColorCompat(R.color.colorThemeAccent)
    }

    /**
     * 设置登陆状态ui
     * @param account 账户信息
     */
    private fun setLoginAccountUi(account: AccountData) {
        binding.ivHeader.setImageResource(R.mipmap.ic_launcher_round)
        binding.tvAccountName.text = account.userInfo.username
        binding.tvLevel.text =
            getString(
                R.string.me_account_info_text,
                account.coinInfo.level,
                account.coinInfo.rank.toString()
            )
        binding.tvLevel.visibility = View.VISIBLE
    }

    /**
     * 设置未登状态ui
     */
    private fun setUnLoginAccountUi() {
        binding.ivHeader.setImageResource(R.drawable.vector_account)
        binding.tvAccountName.text = getString(R.string.me_account_default_text)
        binding.tvLevel.text = ""
        binding.tvLevel.visibility = View.GONE
    }

}