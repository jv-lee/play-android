package com.lee.playandroid

import android.annotation.SuppressLint
import android.view.ViewStub
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.lee.playandroid.base.base.BaseFragment
import com.lee.playandroid.base.extensions.binding
import com.lee.playandroid.base.extensions.getCheckedColorStateListCompat
import com.lee.playandroid.base.extensions.setBackgroundColorCompat
import com.lee.playandroid.base.extensions.toast
import com.lee.playandroid.base.livedatabus.InjectBus
import com.lee.playandroid.base.livedatabus.LiveDataBus
import com.lee.playandroid.base.tools.DarkViewUpdateTools
import com.lee.playandroid.base.utils.LogUtil
import com.lee.playandroid.base.widget.FloatingLayout
import com.lee.playandroid.common.entity.LoginEvent
import com.lee.playandroid.common.entity.NavigationSelectEvent
import com.lee.playandroid.common.ui.extensions.bindNavigationAction
import com.lee.playandroid.databinding.FragmentMainBinding
import com.lee.playandroid.databinding.LayoutStubFloatingBinding
import com.lee.playandroid.router.navigateLogin
import com.lee.playandroid.service.AccountService
import com.lee.playandroid.service.hepler.ModuleService
import kotlinx.coroutines.launch
import com.lee.playandroid.common.R as CR

/**
 * MainFragment 是所有Fragment的容器类
 * @author jv.lee
 * @date 2021/11/2
 */
class MainFragment :
    BaseFragment(R.layout.fragment_main),
    DarkViewUpdateTools.ViewCallback {

    private val binding by binding(FragmentMainBinding::bind)

    private val mainLabels by lazy { resources.getStringArray(R.array.main_labels) }

    private val accountService by lazy { ModuleService.find<AccountService>() }

    override fun bindView() {
        // 设置深色主题控制器监听
        DarkViewUpdateTools.bindViewCallback(viewLifecycleOwner, this)

        // fragment容器与navigationBar绑定
        initNavigation()

        // 加载stub悬浮按钮view
        showFloatingStubView()
    }

    override fun bindData() {
        LiveDataBus.instance.injectBus(this)
    }

    @SuppressLint("ResourceType")
    override fun updateDarkView() {
        binding.navigationBar.itemTextColor = requireContext().getCheckedColorStateListCompat(
            CR.color.colorThemeFocus,
            CR.color.colorThemePrimary
        )
        binding.navigationBar.itemIconTintList = requireContext().getCheckedColorStateListCompat(
            CR.color.colorThemeFocus,
            CR.color.colorThemePrimary
        )
        binding.navigationBar.setBackgroundColorCompat(CR.color.colorThemeItem)
    }

    private fun initNavigation() {
        binding.navigationBar.bindNavigationAction(binding.container, mainLabels) { menuItem, _ ->
            LiveDataBus.instance.getChannel(NavigationSelectEvent::class.java)
                .postValue(NavigationSelectEvent(menuItem.title.toString()))
        }
    }

    private fun showFloatingStubView() {
        if (BuildConfig.DEBUG) {
            val floatingLayout =
                binding.root.findViewById<ViewStub>(R.id.view_stub_floating).inflate()
            val floatingBinding = LayoutStubFloatingBinding.bind(floatingLayout)

            floatingBinding.root.setEventCallback(object : FloatingLayout.EventCallback() {
                override fun onClicked() {
                    toast(getString(com.lee.playandroid.home.R.string.home_header_text))
                }
            })
        }
    }

    @InjectBus
    fun loginEvent(event: LoginEvent) {
        LogUtil.i("loginEvent:$event")
        viewLifecycleOwner.lifecycleScope.launch {
            toast(getString(R.string.login_token_failed))
            accountService.clearLoginState(requireActivity())
            binding.container.findNavController().navigateLogin()
        }
    }
}