package com.lee.pioneer.library.common.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.google.android.material.tabs.TabLayoutMediator
import com.lee.library.adapter.core.UiPager2Adapter
import com.lee.library.adapter.core.UiPagerAdapter
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.increaseOffscreenPageLimit
import com.lee.library.extensions.toast
import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.observeState
import com.lee.library.widget.StatusLayout
import com.lee.pioneer.library.common.R
import com.lee.pioneer.library.common.databinding.FragmentBaseTab2Binding
import com.lee.pioneer.library.common.databinding.FragmentBaseTabBinding
import com.lee.pioneer.library.common.entity.Tab

/**
 * @author jv.lee
 * @data 2021/11/9
 * @description 基础tabFragment类
 */
abstract class BaseTabFragment2 : BaseFragment(R.layout.fragment_base_tab2) {

    private val binding by binding(FragmentBaseTab2Binding::bind)

    private val adapter by lazy {
        UiPagerAdapter(
            childFragmentManager,
            arrayListOf(),
            arrayListOf()
        )
    }


    override fun bindView() {
        binding.statusLayout.setStatus(StatusLayout.STATUS_LOADING)
        binding.statusLayout.setOnReloadListener {
            requestTabs()
        }
    }

    override fun bindData() {
        dataObserveState().observeState<List<Tab>>(viewLifecycleOwner, success = {
            binding.statusLayout.setStatus(StatusLayout.STATUS_DATA)
            bindAdapter(it)
        }, error = {
            toast(it.message)
            binding.statusLayout.setStatus(StatusLayout.STATUS_DATA_ERROR)
        }, loading = {
            binding.statusLayout.setStatus(StatusLayout.STATUS_LOADING)
        })
    }

    /**
     * 根据分类数据构建分页tab及page
     * @param tabsData 分类page数据
     */
    private fun bindAdapter(tabsData: List<Tab>) {
        val fragments = arrayListOf<Fragment>()
        val titles = arrayListOf<String>()

        tabsData.map {
            titles.add(it.name)
            fragments.add(createChildFragment(it.id))
        }

        adapter.fragmentList.clear()
        adapter.tabList.clear()

        adapter.fragmentList.addAll(fragments)
        adapter.tabList.addAll(titles)

        binding.vpContainer.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.vpContainer)
    }

    abstract fun requestTabs()

    abstract fun createChildFragment(id: Long): Fragment

    abstract fun dataObserveState(): LiveData<UiState>

    open fun findBinding() = binding

}