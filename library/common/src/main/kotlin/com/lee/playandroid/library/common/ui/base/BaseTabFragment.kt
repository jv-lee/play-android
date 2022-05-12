package com.lee.playandroid.library.common.ui.base

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.tabs.TabLayoutMediator
import com.lee.library.adapter.core.UiPagerAdapter2
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.increaseOffscreenPageLimit
import com.lee.library.viewstate.collectState
import com.lee.library.widget.StatusLayout
import com.lee.playandroid.library.common.R
import com.lee.playandroid.library.common.databinding.FragmentBaseTabBinding
import com.lee.playandroid.library.common.entity.Tab
import com.lee.playandroid.library.common.extensions.actionFailed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2021/11/9
 * @description 基础tabFragment类
 */
abstract class BaseTabFragment : BaseNavigationFragment(R.layout.fragment_base_tab),
    StatusLayout.OnReloadListener {

    private val binding by binding(FragmentBaseTabBinding::bind)

    private var adapter: UiPagerAdapter2? = null
    private var mediator: TabLayoutMediator? = null

    abstract fun createChildFragment(id: Long): Fragment

    abstract fun requestData()

    abstract fun viewEvents(): Flow<BaseTabViewEvent>

    abstract fun viewStates(): StateFlow<BaseTabViewState>

    open fun findBinding() = binding

    override fun bindView() {
        binding.statusLayout.setOnReloadListener(this)
        binding.tabLayout.increaseOffscreenPageLimit(binding.vpContainer)
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchWhenResumed {
            viewEvents().collect { event ->
                when (event) {
                    is BaseTabViewEvent.RequestFailed -> {
                        actionFailed(event.error)
                        adapter?.getFragments()?.isNullOrEmpty() ?: kotlin.run {
                            binding.statusLayout.setStatus(StatusLayout.STATUS_DATA_ERROR)
                        }
                    }
                }
            }
        }

        viewStates().run {
            launchWhenResumed {
                collectState(BaseTabViewState::tabList) {
                    binding.statusLayout.setStatus(StatusLayout.STATUS_DATA)
                    bindAdapter(it)
                }
            }
            launchWhenResumed {
                collectState(BaseTabViewState::loading) {
                    if (it) binding.statusLayout.postLoading()
                }
            }
        }
    }

    override fun onReload() {
        requestData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediator?.detach()
        binding.vpContainer.adapter = null
        adapter = null
        mediator = null
    }

    /**
     * 根据分类数据构建分页tab及page
     * @param tabsData 分类page数据
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun bindAdapter(tabsData: List<Tab>) {
        // 校验空数据及重复设置
        if (binding.vpContainer.adapter != null || tabsData.isEmpty()) return

        val fragments = arrayListOf<Fragment>()
        val titles = arrayListOf<String>()

        tabsData.forEach {
            titles.add(it.name)
            fragments.add(createChildFragment(it.id))
        }

        adapter = UiPagerAdapter2(childFragmentManager, viewLifecycleOwner.lifecycle)
        adapter?.addAll(fragments)
        binding.vpContainer.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.vpContainer) { tab, position ->
            if (titles.size > position) {
                tab.text = titles[position]
            }
        }.also {
            mediator = it
        }.attach()
    }

}