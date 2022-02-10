package com.lee.playandroid

import android.annotation.SuppressLint
import android.view.ViewStub
import androidx.core.content.ContextCompat
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
import com.lee.playandroid.router.navigateLogin

/**
 * @author jv.lee
 * @date 2021/11/2
 * @description MainFragment 是所有Fragment的容器类
 */
class MainFragment : BaseFragment(R.layout.fragment_main),
    DarkViewUpdateTools.ViewCallback {

    private val binding by binding(FragmentMainBinding::bind)

    override fun bindView() {
        //设置深色主题控制器监听
        DarkViewUpdateTools.bindViewCallback(viewLifecycleOwner, this)

        //fragment容器与navigationBar绑定
        initNavigation()

        // 加载stub悬浮按钮view
        showFloatingStubView()
    }

    override fun bindData() {
        LiveDataBus.getInstance().injectBus(this)
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
        val controller = binding.container.findNavController()
        val mainLabels = resources.getStringArray(R.array.main_labels)
        binding.navigationBar.bindNavigationAction(controller, mainLabels) { menuItem, _ ->
            LiveDataBus.getInstance().getChannel(NavigationSelectEvent.key)
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

    @InjectBus(LoginEvent.key, isActive = true)
    fun loginEvent(event: LoginEvent) {
        toast(getString(R.string.login_token_failed))
        binding.container.findNavController().navigateLogin()
    }

}