package com.lee.playandroid.common.ui.base

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.tabs.TabLayoutMediator
import com.lee.playandroid.base.adapter.core.UiPagerAdapter2
import com.lee.playandroid.base.base.BaseBindingNavigationFragment
import com.lee.playandroid.base.extensions.collectState
import com.lee.playandroid.base.extensions.increaseOffscreenPageLimit
import com.lee.playandroid.base.widget.StatusLayout
import com.lee.playandroid.common.databinding.FragmentBaseTabBinding
import com.lee.playandroid.common.entity.Tab
import com.lee.playandroid.common.extensions.actionFailed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * 基础tabFragment类
 * @author jv.lee
 * @date 2021/11/9
 */
abstract class BaseTabFragment :
    BaseBindingNavigationFragment<FragmentBaseTabBinding>(),
    StatusLayout.OnReloadListener {

    private var adapter: UiPagerAdapter2? = null
    private var mediator: TabLayoutMediator? = null

    abstract fun createChildFragment(id: Long): Fragment

    abstract fun requestData()

    abstract fun viewEvents(): Flow<BaseTabViewEvent>

    abstract fun viewStates(): StateFlow<BaseTabViewState>

    open fun findBinding() = mBinding

    override fun bindView() {
        mBinding.statusLayout.setOnReloadListener(this)
        mBinding.tabLayout.increaseOffscreenPageLimit(mBinding.vpContainer)
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchWhenResumed {
            viewEvents().collect { event ->
                when (event) {
                    is BaseTabViewEvent.RequestFailed -> {
                        actionFailed(event.error)
                        adapter?.getFragments()?.isNullOrEmpty() ?: kotlin.run {
                            mBinding.statusLayout.setStatus(StatusLayout.STATUS_DATA_ERROR)
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
                    mBinding.statusLayout.postLoading()
                    return@collectState
                }
                if (tab.isEmpty()) {
                    mBinding.statusLayout.setStatus(StatusLayout.STATUS_EMPTY_DATA)
                } else {
                    mBinding.statusLayout.setStatus(StatusLayout.STATUS_DATA)
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
        mBinding.vpContainer.adapter = null
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
        if (mBinding.vpContainer.adapter != null || tabsData.isEmpty()) return

        val fragments = arrayListOf<Fragment>()
        val titles = arrayListOf<String>()

        tabsData.forEach {
            titles.add(it.name)
            fragments.add(createChildFragment(it.id))
        }

        adapter = UiPagerAdapter2(childFragmentManager, viewLifecycleOwner.lifecycle)
        adapter?.addAll(fragments)
        mBinding.vpContainer.adapter = adapter

        mBinding.tabLayout.visibility = View.VISIBLE
        TabLayoutMediator(mBinding.tabLayout, mBinding.vpContainer) { tab, position ->
            if (titles.size > position) {
                tab.text = titles[position]
            }
        }.also {
            mediator = it
        }.attach()
    }
}