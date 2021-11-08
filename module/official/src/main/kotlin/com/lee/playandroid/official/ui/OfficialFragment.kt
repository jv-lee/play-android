package com.lee.playandroid.official.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.lee.library.adapter.core.UiPager2Adapter
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.toast
import com.lee.library.mvvm.ui.observeState
import com.lee.library.widget.StatusLayout
import com.lee.pioneer.library.common.entity.OfficialTab
import com.lee.playandroid.official.R
import com.lee.playandroid.official.databinding.FragmentOfficialBinding
import com.lee.playandroid.official.viewmodel.OfficialViewModel

/**
 * @author jv.lee
 * @data 2021/11/2
 * @description
 */
class OfficialFragment : BaseNavigationFragment(R.layout.fragment_official) {

    private val viewModel by viewModels<OfficialViewModel>()

    private val binding by binding(FragmentOfficialBinding::bind)

    private var adapter: UiPager2Adapter? = null
    private var mediator: TabLayoutMediator? = null

    override fun bindView() {
        binding.statusLayout.setStatus(StatusLayout.STATUS_LOADING)
        binding.statusLayout.setOnReloadListener {
            viewModel.requestTabs()
        }
    }

    override fun bindData() {
        viewModel.tabsLive.observeState<List<OfficialTab>>(viewLifecycleOwner, success = {
            binding.statusLayout.setStatus(StatusLayout.STATUS_DATA)
            bindAdapter(it)
        }, error = {
            toast(it.message)
            binding.statusLayout.setStatus(StatusLayout.STATUS_DATA_ERROR)
        }, loading = {
            binding.statusLayout.setStatus(StatusLayout.STATUS_LOADING)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mediator?.detach()
    }

    /**
     * 根据分类数据构建分页tab及page
     * @param tabsData 分类page数据
     */
    private fun bindAdapter(tabsData: List<OfficialTab>) {
        val fragments = arrayListOf<Fragment>()
        val titles = arrayListOf<String>()

        tabsData.map {
            titles.add(it.name)
            fragments.add(OfficialListFragment.newInstance(it.id))
        }

        binding.vpContainer.offscreenPageLimit = titles.size
        binding.vpContainer.isSaveEnabled = false
        binding.vpContainer.adapter = UiPager2Adapter(this, fragments).also {
            adapter = it
        }

        TabLayoutMediator(binding.tabLayout, binding.vpContainer) { tab, position ->
            tab.text = titles[position]
        }.also {
            mediator = it
        }.attach()
    }

    override fun lazyLoad() {
        viewModel.requestTabs()
    }

}