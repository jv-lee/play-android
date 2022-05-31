package com.lee.playandroid

import android.annotation.SuppressLint
import android.view.ViewStub
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.setBackgroundColorCompat
import com.lee.library.extensions.toast
import com.lee.library.livedatabus.InjectBus
import com.lee.library.livedatabus.LiveDataBus
import com.lee.library.tools.DarkViewUpdateTools
import com.lee.library.widget.FloatingLayout
import com.lee.playandroid.databinding.FragmentMainBinding
import com.lee.playandroid.databinding.LayoutStubFloatingBinding
import com.lee.playandroid.library.common.entity.LoginEvent
import com.lee.playandroid.library.common.entity.NavigationSelectEvent
import com.lee.playandroid.library.common.ui.extensions.bindNavigationAction
import com.lee.playandroid.library.service.AccountService
import com.lee.playandroid.library.service.hepler.ModuleService
import com.lee.playandroid.router.navigateLogin
import kotlinx.coroutines.launch

/**
 * MainFragment 是所有Fragment的容器类
 * @author jv.lee
 * @date 2021/11/2
 */
class MainFragment : BaseFragment(R.layout.fragment_main),
    DarkViewUpdateTools.ViewCallback {

    private val binding by binding(FragmentMainBinding::bind)

    private val mainLabels by lazy { resources.getStringArray(R.array.main_labels) }

    override fun bindView() {
        //设置深色主题控制器监听
        DarkViewUpdateTools.bindViewCallback(viewLifecycleOwner, this)

        //fragment容器与navigationBar绑定
        initNavigation()

        // 加载stub悬浮按钮view
        showFloatingStubView()
    }

    override fun bindData() {
        LiveDataBus.instance.injectBus(this)
    }

    @SuppressLint("ResourceType")
    override fun updateDarkView() {
        binding.navigationBar.itemTextColor =
            ContextCompat.getColorStateList(requireContext(), R.drawable.selector_main)
        binding.navigationBar.itemIconTintList =
            ContextCompat.getColorStateList(requireContext(), R.drawable.selector_main)
        binding.navigationBar.setBackgroundColorCompat(R.color.colorThemeItem)
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
                    toast("welcome to play android ~")
                }
            })
        }
    }

    @InjectBus
    fun loginEvent(event: LoginEvent) {
        viewLifecycleOwner.lifecycleScope.launch {
            toast(getString(R.string.login_token_failed))
            ModuleService.find<AccountService>().requestLogout(requireActivity())
            binding.container.findNavController().navigateLogin()
        }
    }

}