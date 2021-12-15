package com.lee.playandroid

import androidx.fragment.app.Fragment
import com.lee.library.adapter.core.UiPager2Adapter
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.playandroid.databinding.FragmentSingleMainBinding
import com.lee.playandroid.home.view.HomeFragment
import com.lee.playandroid.me.ui.fragment.MeFragment
import com.lee.playandroid.system.ui.SystemFragment

/**
 * @author jv.lee
 * @date 2021/11/24
 * @description
 */
@Deprecated("放弃使用单Navigation回退栈模式")
class SingleMainFragment : BaseFragment(R.layout.fragment_single_main) {

    private val binding by binding(FragmentSingleMainBinding::bind)

    private val fragments = arrayListOf<Fragment>(
        HomeFragment(),
        SystemFragment(),
        MeFragment()
    )

    override fun bindView() {
        binding.container.adapter = UiPager2Adapter(this, fragments)
        binding.container.isUserInputEnabled = false

        binding.navigationBar.bindViewPager(binding.container)
    }

    override fun bindData() {

    }

}