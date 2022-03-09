package com.lee.playandroid.system.ui

import android.annotation.SuppressLint
import com.lee.library.adapter.core.UiPagerAdapter2
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.bindRadioGroup
import com.lee.library.extensions.binding
import com.lee.playandroid.library.common.ui.extensions.setThemeGradientBackground
import com.lee.playandroid.system.R
import com.lee.playandroid.system.databinding.FragmentSystemBinding

/**
 * @author jv.lee
 * @date 2021/11/2
 * @description 首页第三个Tab 体系页面
 */
class SystemFragment : BaseNavigationFragment(R.layout.fragment_system) {

    private val binding by binding(FragmentSystemBinding::bind)

    private var mAdapter: UiPagerAdapter2? = null

    override fun bindView() {
        binding.toolbar.setThemeGradientBackground()
        binding.radioSystem.setButtonDrawable(android.R.color.transparent)
        binding.radioNavigation.setButtonDrawable(android.R.color.transparent)
        binding.vpContainer.bindRadioGroup(binding.radioTabLayout)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun bindData() {
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
    fun parentBindingAction(action: (FragmentSystemBinding) -> Unit) {
        action(binding)
    }

}