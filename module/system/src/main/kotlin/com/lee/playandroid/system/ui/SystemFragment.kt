package com.lee.playandroid.system.ui

import com.lee.library.adapter.core.UiPager2Adapter
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.bindRadioGroup
import com.lee.library.extensions.binding
import com.lee.playandroid.system.R
import com.lee.playandroid.system.databinding.FragmentSystemBinding

/**
 * @author jv.lee
 * @data 2021/11/2
 * @description
 */
class SystemFragment : BaseFragment(R.layout.fragment_system) {

    private val binding by binding(FragmentSystemBinding::bind)

    private val mAdapter by lazy {
        UiPager2Adapter(this, mutableListOf(SystemContentFragment(), NavigationFragment()))
    }

    override fun bindView() {
        binding.vpContainer.adapter = mAdapter
        binding.vpContainer.bindRadioGroup(binding.radioTabLayout)
    }

    override fun bindData() {

    }
}