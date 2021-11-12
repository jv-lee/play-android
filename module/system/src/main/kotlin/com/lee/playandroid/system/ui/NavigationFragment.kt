package com.lee.playandroid.system.ui

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.findParentFragment
import com.lee.library.extensions.setMargin
import com.lee.library.extensions.toast
import com.lee.library.mvvm.ui.observeState
import com.lee.playandroid.library.common.entity.NavigationItem
import com.lee.playandroid.system.R
import com.lee.playandroid.system.databinding.FragmentNavigationBinding
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

    override fun bindView() {
        mNavigationTabAdapter = NavigationTabAdapter(requireContext(), arrayListOf())

        findParentFragment<SystemFragment>()?.parentBindingAction {
            binding.rvTab.setMargin(top = it.toolbar.getToolbarLayoutHeight())
        }
        binding.rvTab.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTab.adapter = mNavigationTabAdapter

        mNavigationTabAdapter.setOnItemClickListener { _, _, position ->
            mNavigationTabAdapter.selectItem(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun bindData() {
        viewModel.navigationLive.observeState<List<NavigationItem>>(this, success = {
            mNavigationTabAdapter.updateData(it)
            mNavigationTabAdapter.notifyDataSetChanged()
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