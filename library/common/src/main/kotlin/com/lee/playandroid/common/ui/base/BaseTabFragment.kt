package com.lee.playandroid.common.ui.base

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.tabs.TabLayoutMediator
import com.lee.playandroid.base.adapter.core.UiPagerAdapter2
import com.lee.playandroid.base.base.BaseNavigationFragment
import com.lee.playandroid.base.extensions.binding
import com.lee.playandroid.base.extensions.increaseOffscreenPageLimit
import com.lee.playandroid.base.viewstate.collectState
import com.lee.playandroid.base.widget.StatusLayout
import com.lee.playandroid.common.R
import com.lee.playandroid.common.databinding.FragmentBaseTabBinding
import com.lee.playandroid.common.entity.Tab
import com.lee.playandroid.common.extensions.actionFailed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

/**
 * 基础tabFragment类
 * @author jv.lee
 * @date 2021/11/9
 */
abstract class BaseTabFragment :
    BaseNavigationFragment(R.layout.fragment_base_tab),
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

        launchWhenResumed {
            viewStates().collectState(
                BaseTabViewState::loading,
                BaseTabViewState::tabList
            ) { loading, tab ->
                if (loading) {
                    binding.statusLayout.postLoading()
                    return@collectState
                }
                if (tab.isEmpty()) {
                    binding.statusLayout.setStatus(StatusLayout.STATUS_EMPTY_DATA)
                } else {
                    binding.statusLayout.setStatus(StatusLayout.STATUS_DATA)
                    bindAdapter(tab)
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

        binding.tabLayout.visibility = View.VISIBLE
        TabLayoutMediator(binding.tabLayout, binding.vpContainer) { tab, position ->
            if (titles.size > position) {
                tab.text = titles[position]
            }
        }.also {
            mediator = it
        }.attach()
    }
}