package com.lee.playandroid.system.ui

import androidx.lifecycle.LifecycleCoroutineScope
import com.lee.playandroid.base.adapter.core.UiPagerAdapter2
import com.lee.playandroid.base.base.BaseNavigationFragment
import com.lee.playandroid.base.extensions.bindRadioGroup
import com.lee.playandroid.base.extensions.binding
import com.lee.playandroid.base.extensions.delayBackEvent
import com.lee.playandroid.common.ui.extensions.setThemeGradientBackground
import com.lee.playandroid.system.R
import com.lee.playandroid.system.databinding.FragmentSystemBinding

/**
 * 首页第三个Tab 体系页面
 * @author jv.lee
 * @date 2021/11/2
 */
class SystemFragment : BaseNavigationFragment(R.layout.fragment_system) {

    private val binding by binding(FragmentSystemBinding::bind)

    private var mAdapter: UiPagerAdapter2? = null

    override fun bindView() {
        // 拦截back处理
        delayBackEvent()
        // 设置toolbar主题渐变色背景
        binding.toolbar.setThemeGradientBackground()

        binding.radioSystem.setButtonDrawable(android.R.color.transparent)
        binding.radioNavigation.setButtonDrawable(android.R.color.transparent)
        binding.vpContainer.bindRadioGroup(binding.radioTabLayout)
    }

    override fun LifecycleCoroutineScope.bindData() {
        mAdapter = UiPagerAdapter2(childFragmentManager, viewLifecycleOwner.lifecycle)
        mAdapter?.addAll(mutableListOf(SystemContentFragment(), NavigationContentFragment()))
        binding.vpContainer.adapter = mAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.vpContainer.adapter = null
        mAdapter = null
    }

    /**
     * 提供给子类操作父Fragment bindingUi控制
     * @param action 父Fragment binding作用域
     */
    fun parentBindingAction(action: FragmentSystemBinding.() -> Unit) {
        binding.action()
    }

}