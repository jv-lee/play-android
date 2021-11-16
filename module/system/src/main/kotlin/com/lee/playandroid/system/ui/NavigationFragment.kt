package com.lee.playandroid.system.ui

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import com.lee.library.adapter.page.submitSinglePage
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.findParentFragment
import com.lee.library.extensions.toast
import com.lee.library.mvvm.ui.observeState
import com.lee.playandroid.library.common.entity.NavigationItem
import com.lee.playandroid.library.common.ui.widget.OffsetItemDecoration
import com.lee.playandroid.system.R
import com.lee.playandroid.system.databinding.FragmentNavigationBinding
import com.lee.playandroid.system.ui.adapter.NavigationContentAdapter
import com.lee.playandroid.system.ui.adapter.NavigationTabAdapter
import com.lee.playandroid.system.viewmodel.NavigationViewModel

/**
 * @author jv.lee
 * @data 2021/11/10
 * @description  导航Fragment
 * @see SystemFragment 体系Fragment下第二个Tab
 */
class NavigationFragment : BaseFragment(R.layout.fragment_navigation) {

    private val viewModel by viewModels<NavigationViewModel>()

    private val binding by binding(FragmentNavigationBinding::bind)

    private lateinit var mNavigationTabAdapter: NavigationTabAdapter
    private lateinit var mNavigationContentAdapter: NavigationContentAdapter

    override fun bindView() {
        findParentFragment<SystemFragment>()?.parentBindingAction {
            val decoration =
                OffsetItemDecoration(
                    offsetTop = it.toolbar.getToolbarLayoutHeight(),
                    offsetBottom = resources.getDimension(R.dimen.navigation_bar_height).toInt()
                )
            binding.rvTab.addItemDecoration(decoration)
            binding.rvContainer.addItemDecoration(decoration)
        }

        mNavigationTabAdapter = NavigationTabAdapter(arrayListOf())
        binding.rvTab.adapter = mNavigationTabAdapter

        mNavigationContentAdapter = NavigationContentAdapter(requireContext(), arrayListOf())
        binding.rvContainer.adapter = mNavigationContentAdapter.proxy
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun bindData() {
        viewModel.navigationLive.observeState<List<NavigationItem>>(this, success = {
            mNavigationTabAdapter.updateNotify(it)
            mNavigationContentAdapter.submitSinglePage(it)
        }, error = {
            toast(it.message)
        }, loading = {
        })
    }

    override fun lazyLoad() {
        viewModel.requestNavigationData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}