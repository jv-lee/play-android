package com.lee.playandroid.system.ui

import androidx.fragment.app.Fragment
import com.lee.library.adapter.core.UiPager2Adapter
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.bindRadioGroup
import com.lee.library.extensions.binding
import com.lee.library.extensions.delayBackEvent
import com.lee.playandroid.system.R
import com.lee.playandroid.system.databinding.FragmentSystemBinding

/**
 * @author jv.lee
 * @data 2021/11/2
 * @description 首页第二个Tab 体系Fragment
 */
class SystemFragment : BaseFragment(R.layout.fragment_system) {

    private val binding by binding(FragmentSystemBinding::bind)

    private val fragmentList: MutableList<Fragment> =
        mutableListOf(SystemContentFragment(), NavigationFragment())

    override fun bindView() {
        delayBackEvent()

        binding.vpContainer.adapter = UiPager2Adapter(this, fragmentList)
        binding.vpContainer.bindRadioGroup(binding.radioTabLayout)
    }

    override fun bindData() {
    }

    /**
     * 提供给子类操作父Fragment bindingUi控制
     * @param action 父Fragment binding作用域
     */
    fun parentBindingAction(action: (FragmentSystemBinding) -> Unit) {
        action(binding)
    }

}