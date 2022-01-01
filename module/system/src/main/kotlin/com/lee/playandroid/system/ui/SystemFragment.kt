package com.lee.playandroid.system.ui

import androidx.fragment.app.viewModels
import com.lee.library.adapter.core.UiPager2Adapter
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.bindRadioGroup
import com.lee.library.extensions.binding
import com.lee.library.extensions.delayBackEvent
import com.lee.playandroid.system.R
import com.lee.playandroid.system.databinding.FragmentSystemBinding
import com.lee.playandroid.system.viewmodel.SystemViewModel

/**
 * @author jv.lee
 * @date 2021/11/2
 * @description 首页第三个Tab 体系页面
 */
class SystemFragment : BaseFragment(R.layout.fragment_system) {

    private val viewModel by viewModels<SystemViewModel>()

    private val binding by binding(FragmentSystemBinding::bind)

    override fun bindView() {
        delayBackEvent()
        binding.vpContainer.bindRadioGroup(binding.radioTabLayout)
    }

    override fun bindData() {
        viewModel.fragmentsLive.observe(this, {
            binding.vpContainer.adapter = UiPager2Adapter(this, it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.vpContainer.removeAllViews()
    }

    /**
     * 提供给子类操作父Fragment bindingUi控制
     * @param action 父Fragment binding作用域
     */
    fun parentBindingAction(action: (FragmentSystemBinding) -> Unit) {
        action(binding)
    }

}