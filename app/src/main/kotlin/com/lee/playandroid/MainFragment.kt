package com.lee.playandroid

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.setBackgroundColorCompat
import com.lee.library.extensions.toast
import com.lee.library.livedatabus.InjectBus
import com.lee.library.livedatabus.LiveDataBus
import com.lee.library.tools.DarkViewUpdateTools
import com.lee.playandroid.databinding.FragmentMainBinding
import com.lee.playandroid.library.common.entity.LoginEvent
import com.lee.playandroid.library.common.entity.NavigationSelectEvent
import com.lee.playandroid.library.common.tools.bindNavigationAction
import com.lee.playandroid.router.navigateLogin

/**
 * @author jv.lee
 * @date 2021/11/2
 * @description MainFragment 是所有Fragment的容器类
 */
class MainFragment : BaseFragment(R.layout.fragment_main),
    DarkViewUpdateTools.ViewCallback {

    private val binding by binding(FragmentMainBinding::bind)

    private val labels by lazy {
        arrayListOf(
            getString(R.string.nav_home),
            getString(R.string.nav_square),
            getString(R.string.nav_system),
            getString(R.string.nav_me)
        )
    }

    override fun bindView() {
        //设置深色主题控制器监听
        DarkViewUpdateTools.bindViewCallback(viewLifecycleOwner, this)

        //fragment容器与navigationBar绑定
        binding.navigationBar.bindNavigationAction(binding.container, labels) { menuItem, _ ->
            LiveDataBus.getInstance().getChannel(NavigationSelectEvent.key)
                .postValue(NavigationSelectEvent(menuItem.title.toString()))
        }
    }

    override fun bindData() {
        LiveDataBus.getInstance().injectBus(this)
    }

    @SuppressLint("ResourceType")
    override fun updateDarkView() {
        binding.navigationBar.itemTextColor =
            ContextCompat.getColorStateList(requireContext(), R.drawable.main_selector)
        binding.navigationBar.itemIconTintList =
            ContextCompat.getColorStateList(requireContext(), R.drawable.main_selector)
        binding.navigationBar.setBackgroundColorCompat(R.color.colorThemeItem)
    }

    @InjectBus(LoginEvent.key, isActive = true)
    fun loginEvent(event: LoginEvent) {
        toast(getString(R.string.login_token_failed))
        binding.container.findNavController().navigateLogin()
    }

}