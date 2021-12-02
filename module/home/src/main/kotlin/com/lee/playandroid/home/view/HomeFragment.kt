package com.lee.playandroid.home.view

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.listener.LoadErrorListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseFragment
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.*
import com.lee.library.livedatabus.InjectBus
import com.lee.library.livedatabus.LiveDataBus
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.observeState
import com.lee.library.tools.DarkViewUpdateTools
import com.lee.playandroid.home.R
import com.lee.playandroid.home.bean.HomeContent
import com.lee.playandroid.home.databinding.FragmentHomeBinding
import com.lee.playandroid.home.view.adapter.ContentAdapter
import com.lee.playandroid.home.viewmodel.HomeViewModel
import com.lee.playandroid.library.common.entity.NavigationSelectEvent
import com.lee.playandroid.library.common.entity.PageUiData
import com.lee.playandroid.library.common.ui.widget.OffsetItemDecoration
import com.lee.playandroid.router.navigateDetails
import com.lee.playandroid.router.navigateSearch

/**
 * @author jv.lee
 * @date 2021/11/2
 * @description 首页第一个Tab HomeFragment
 */
class HomeFragment : BaseFragment(R.layout.fragment_home),
    DarkViewUpdateTools.ViewCallback {

    private val viewModel by viewModels<HomeViewModel>()

    private val binding by binding(FragmentHomeBinding::bind)

    private lateinit var mAdapter: ContentAdapter

    override fun bindView() {
        delayBackEvent()
        DarkViewUpdateTools.bindViewCallback(this, this)

        binding.ivSearch.setOnClickListener {
            findNavController().navigateSearch()
        }

        binding.refreshView.setOnRefreshListener {
            mAdapter.openLoadMore()
            viewModel.loadListData(LoadStatus.REFRESH)
        }

        binding.rvContainer.addItemDecoration(OffsetItemDecoration(binding.toolbar.getToolbarLayoutHeight()))
        binding.rvContainer.adapter = ContentAdapter(requireContext(), arrayListOf()).apply {
            mAdapter = this
            initStatusView()
            pageLoading()
            setAutoLoadMoreListener {
                viewModel.loadListData(LoadStatus.LOAD_MORE)
            }
            setLoadErrorListener(object : LoadErrorListener {
                override fun pageReload() {
                    viewModel.loadListData(LoadStatus.REFRESH)
                }

                override fun itemReload() {
                    viewModel.loadListData(LoadStatus.RELOAD)
                }
            })
            setOnItemClickListener { _, entity, _ ->
                entity?.content?.apply {
                    findNavController().navigateDetails(title, link)
                }
            }
        }.proxy
    }

    override fun bindData() {
        LiveDataBus.getInstance().injectBus(this)

        viewModel.contentListLive.observeState<PageUiData<HomeContent>>(this, success = {
            binding.refreshView.isRefreshing = false
            mAdapter.submitData(it, diff = true)
        }, error = {
            toast(it.toString())
            binding.refreshView.isRefreshing = false
            mAdapter.submitFailed()
        })
    }

    @InjectBus(NavigationSelectEvent.key, isActive = true)
    fun navigationEvent(event: NavigationSelectEvent) {
        if (event.title == getString(R.string.nav_home) && isResumed) {
            binding.rvContainer.smoothScrollToTop()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun updateDarkView() {
        if (isResumed) {
            binding.refreshView.setBackgroundColorCompat(R.color.colorThemeBackground)
            binding.tvTitle.setTextColorCompat(R.color.colorThemeAccent)
            mAdapter.reInitStatusView()
            mAdapter.notifyDataSetChanged()
        }
    }

}