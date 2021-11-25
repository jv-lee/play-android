package com.lee.playandroid

import androidx.fragment.app.Fragment
import com.lee.library.adapter.core.UiPager2Adapter
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.playandroid.databinding.FragmentRootBinding
import com.lee.playandroid.home.view.HomeFragment
import com.lee.playandroid.me.ui.fragment.MeFragment
import com.lee.playandroid.system.ui.SystemFragment

/**
 * @author jv.lee
 * @date 2021/11/24
 * @description
 */
@Deprecated("")
class RootFragment : BaseFragment(R.layout.fragment_root) {

    private val binding by binding(FragmentRootBinding::bind)

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