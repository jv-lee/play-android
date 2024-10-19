package com.lee.playandroid.me.ui.fragment

import android.Manifest
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.imagetools.select.ImageLaunch
import com.imagetools.select.entity.SelectConfig
import com.imagetools.select.entity.TakeConfig
import com.lee.playandroid.base.base.BaseBindingNavigationFragment
import com.lee.playandroid.base.extensions.collectState
import com.lee.playandroid.base.extensions.delayBackEvent
import com.lee.playandroid.base.extensions.getColorStateListCompat
import com.lee.playandroid.base.extensions.setBackgroundColorCompat
import com.lee.playandroid.base.extensions.setTextColorCompat
import com.lee.playandroid.base.extensions.toast
import com.lee.playandroid.base.tools.DarkViewUpdateTools
import com.lee.playandroid.base.tools.PermissionLauncher
import com.lee.playandroid.base.tools.ShakeHelper
import com.lee.playandroid.common.entity.AccountData
import com.lee.playandroid.common.entity.AccountViewState
import com.lee.playandroid.me.BuildConfig
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentMeBinding
import com.lee.playandroid.me.viewmodel.MeViewModel
import com.lee.playandroid.router.navigateLogin
import com.lee.playandroid.router.navigateMyShare
import com.lee.playandroid.router.navigateTodo
import com.lee.playandroid.common.R as CR

/**
 * 首页第四个Tab 我的页面
 * @author jv.lee
 * @date 2021/11/2
 */
class MeFragment :
    BaseBindingNavigationFragment<FragmentMeBinding>(),
    View.OnClickListener,
    View.OnLongClickListener,
    DarkViewUpdateTools.ViewCallback {

    private val viewModel by viewModels<MeViewModel>()

    private val imageLaunch = ImageLaunch(this)
    private val permissionLaunch = PermissionLauncher(this)

    override fun bindView() {
        // 拦截back处理
        delayBackEvent()
        // 监听当前深色主题变化
        DarkViewUpdateTools.bindViewCallback(viewLifecycleOwner, this)

        mBinding.toolbarLayout.setOnClickListener(this)
        mBinding.lineIntegral.setOnClickListener(this)
        mBinding.lineCollect.setOnClickListener(this)
        mBinding.lineShare.setOnClickListener(this)
        mBinding.lineTodo.setOnClickListener(this)
        mBinding.lineSettings.setOnClickListener(this)

        if (BuildConfig.DEBUG) {
            mBinding.lineIntegral.setOnLongClickListener(this)
            mBinding.lineCollect.setOnLongClickListener(this)
            mBinding.lineShare.setOnLongClickListener(this)
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchOnLifecycle {
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
        ShakeHelper.run {
            // 无需校验登陆状态
            if (view == mBinding.lineSettings) {
                findNavController().navigate(R.id.action_me_fragment_to_settings_fragment)
                return@run
            }

            // 需要校验登陆状态
            if (viewModel.accountService.isLogin()) {
                when (view) {
                    mBinding.lineIntegral ->
                        findNavController().navigate(R.id.action_me_fragment_to_coin_fragment)
                    mBinding.lineCollect ->
                        findNavController().navigate(R.id.action_me_fragment_to_collect_fragment)
                    mBinding.lineShare ->
                        findNavController().navigateMyShare()
                    mBinding.lineTodo ->
                        findNavController().navigateTodo()
                }
            } else {
                toast(getString(CR.string.login_message))
                findNavController().navigateLogin()
            }
        }
    }

    override fun onLongClick(v: View?): Boolean {
        when (v) {
            mBinding.lineIntegral -> {
                permissionLaunch.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, {
                    imageLaunch.select(
                        SelectConfig(isMultiple = false, isCompress = false, columnCount = 3)
                    ) {
                        toast(it[0].uri.toString())
                    }
                })
            }
            mBinding.lineCollect -> {
                permissionLaunch.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, {
                    imageLaunch.select(SelectConfig(isMultiple = true, isCompress = false)) {
                        toast(it[0].uri.toString())
                    }
                })
            }
            mBinding.lineShare -> {
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
        mBinding.constRoot.setBackgroundColorCompat(CR.color.colorThemeBackground)
        mBinding.toolbarLayout.setBackgroundColorCompat(CR.color.colorThemeItem)
        mBinding.tvAccountName.setTextColorCompat(CR.color.colorThemeAccent)
        mBinding.tvLevel.setTextColorCompat(CR.color.colorThemeFocus)
        mBinding.ivHeader.strokeColor =
            requireContext().getColorStateListCompat(CR.color.colorThemeFocus)

        mBinding.lineIntegral.setBackgroundColorCompat(CR.color.colorThemeItem)
        mBinding.lineIntegral.getLeftTextView().setTextColorCompat(CR.color.colorThemeAccent)

        mBinding.lineCollect.setBackgroundColorCompat(CR.color.colorThemeItem)
        mBinding.lineCollect.getLeftTextView().setTextColorCompat(CR.color.colorThemeAccent)

        mBinding.lineShare.setBackgroundColorCompat(CR.color.colorThemeItem)
        mBinding.lineShare.getLeftTextView().setTextColorCompat(CR.color.colorThemeAccent)

        mBinding.lineTodo.setBackgroundColorCompat(CR.color.colorThemeItem)
        mBinding.lineTodo.getLeftTextView().setTextColorCompat(CR.color.colorThemeAccent)

        mBinding.lineSettings.setBackgroundColorCompat(CR.color.colorThemeItem)
        mBinding.lineSettings.getLeftTextView().setTextColorCompat(CR.color.colorThemeAccent)
    }

    /**
     * 设置登陆状态ui
     * @param account 账户信息
     */
    private fun setLoginAccountUi(account: AccountData) {
        mBinding.ivHeader.setImageResource(CR.mipmap.ic_launcher_round)
        mBinding.tvAccountName.text = account.userInfo.username
        mBinding.tvLevel.text =
            getString(R.string.me_account_info_text, account.coinInfo.level, account.coinInfo.rank)
        mBinding.tvLevel.visibility = View.VISIBLE
    }

    /**
     * 设置未登状态ui
     */
    private fun setUnLoginAccountUi() {
        mBinding.ivHeader.setImageResource(R.drawable.vector_account)
        mBinding.tvAccountName.text = getString(R.string.me_account_default_text)
        mBinding.tvLevel.text = ""
        mBinding.tvLevel.visibility = View.GONE
    }
}