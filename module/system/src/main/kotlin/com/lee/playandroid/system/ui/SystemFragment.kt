package com.lee.playandroid.system.ui

import androidx.lifecycle.LifecycleCoroutineScope
import com.lee.playandroid.base.adapter.core.UiPagerAdapter2
import com.lee.playandroid.base.base.BaseBindingNavigationFragment
import com.lee.playandroid.base.extensions.bindRadioGroup
import com.lee.playandroid.base.extensions.delayBackEvent
import com.lee.playandroid.common.ui.extensions.setThemeGradientBackground
import com.lee.playandroid.system.databinding.FragmentSystemBinding

/**
 * 首页第三个Tab 体系页面
 * @author jv.lee
 * @date 2021/11/2
 */
class SystemFragment : BaseBindingNavigationFragment<FragmentSystemBinding>() {

    private var mAdapter: UiPagerAdapter2? = null

    override fun bindView() {
        // 拦截back处理
        delayBackEvent()
        // 设置toolbar主题渐变色背景
        mBinding.toolbar.setThemeGradientBackground()

        mBinding.radioSystem.setButtonDrawable(android.R.color.transparent)
        mBinding.radioNavigation.setButtonDrawable(android.R.color.transparent)
        mBinding.vpContainer.bindRadioGroup(mBinding.radioTabLayout)
    }

    override fun LifecycleCoroutineScope.bindData() {
        mAdapter = UiPagerAdapter2(childFragmentManager, viewLifecycleOwner.lifecycle)
        mAdapter?.addAll(mutableListOf(SystemContentFragment(), NavigationContentFragment()))
        mBinding.vpContainer.adapter = mAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.vpContainer.adapter = null
        mAdapter = null
    }

    /**
     * 提供给子类操作父Fragment bindingUi控制
     * @param action 父Fragment binding作用域
     */
    fun parentBindingAction(action: FragmentSystemBinding.() -> Unit) {
        mBinding.action()
    }
}