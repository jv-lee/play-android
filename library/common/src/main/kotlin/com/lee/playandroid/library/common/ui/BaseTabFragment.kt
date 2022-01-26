package com.lee.playandroid.library.common.ui

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.lee.library.adapter.core.UiPagerAdapter2
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.increaseOffscreenPageLimit
import com.lee.library.mvvm.ui.UiStateLiveData
import com.lee.library.mvvm.ui.observeState
import com.lee.library.widget.StatusLayout
import com.lee.playandroid.library.common.R
import com.lee.playandroid.library.common.databinding.FragmentBaseTabBinding
import com.lee.playandroid.library.common.entity.Tab
import com.lee.playandroid.library.common.extensions.actionFailed

/**
 * @author jv.lee
 * @date 2021/11/9
 * @description 基础tabFragment类
 */
abstract class BaseTabFragment : BaseFragment(R.layout.fragment_base_tab),
    StatusLayout.OnReloadListener {

    private val binding by binding(FragmentBaseTabBinding::bind)

    private var adapter: UiPagerAdapter2? = null
    private var mediator: TabLayoutMediator? = null

    abstract fun requestTabs()

    abstract fun createChildFragment(id: Long): Fragment

    abstract fun dataObserveState(): UiStateLiveData

    open fun findBinding() = binding

    override fun bindView() {
        binding.statusLayout.setOnReloadListener(this)
        binding.vpContainer.increaseOffscreenPageLimit()
    }

    override fun bindData() {
        dataObserveState().observeState<List<Tab>>(viewLifecycleOwner, success = {
            binding.statusLayout.setStatus(StatusLayout.STATUS_DATA)
            bindAdapter(it)
        }, error = {
            adapter?.getFragments()?.isNullOrEmpty() ?: kotlin.run {
                binding.statusLayout.setStatus(StatusLayout.STATUS_DATA_ERROR)
            }
            actionFailed(it)
        }, loading = {
            binding.statusLayout.postLoading()
        })
    }

    override fun onReload() {
        requestTabs()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediator?.detach()
    }

    /**
     * 根据分类数据构建分页tab及page
     * @param tabsData 分类page数据
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun bindAdapter(tabsData: List<Tab>) {
        val fragments = arrayListOf<Fragment>()
        val titles = arrayListOf<String>()

        tabsData.map {
            titles.add(it.name)
            fragments.add(createChildFragment(it.id))
        }

        if (binding.vpContainer.adapter == null) {
            adapter = UiPagerAdapter2(childFragmentManager, viewLifecycleOwner.lifecycle)
            adapter?.addAll(fragments)
            binding.vpContainer.adapter = adapter
        } else {
            adapter?.addAll(fragments)
            adapter?.notifyDataSetChanged()
        }

        TabLayoutMediator(binding.tabLayout, binding.vpContainer) { tab, position ->
            tab.text = titles[position]
        }.also {
            mediator = it
        }.attach()
    }

}