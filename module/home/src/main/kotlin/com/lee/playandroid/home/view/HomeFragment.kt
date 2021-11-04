package com.lee.playandroid.home.view

import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lee.library.adapter.listener.LoadErrorListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.inflate
import com.lee.library.extensions.toast
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.observeState
import com.lee.library.widget.banner.holder.CardImageCreateHolder
import com.lee.pioneer.library.common.entity.Banner
import com.lee.pioneer.library.common.entity.Content
import com.lee.pioneer.library.common.tools.GlideTools
import com.lee.playandroid.home.R
import com.lee.playandroid.home.databinding.FragmentHomeBinding
import com.lee.playandroid.home.databinding.LayoutHomeBannerBinding
import com.lee.playandroid.home.view.adapter.ContentAdapter
import com.lee.playandroid.home.view.adapter.HomeContent
import com.lee.playandroid.home.view.adapter.PageData
import com.lee.playandroid.home.viewmodel.HomeViewModel

/**
 * @author jv.lee
 * @data 2021/11/2
 * @description
 */
class HomeFragment : BaseNavigationFragment(R.layout.fragment_home) {

    private val viewModel by viewModels<HomeViewModel>()

    private val binding by binding(FragmentHomeBinding::bind)

    private val headerBinding by inflate(LayoutHomeBannerBinding::inflate)

    private val mAdapter by lazy { ContentAdapter(requireContext(), arrayListOf()) }

    override fun bindView() {
        binding.rvContainer.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter.proxy
        }

        binding.refreshView.setOnRefreshListener {
            mAdapter.openLoadMore()
            viewModel.requestBanner()
            viewModel.loadListData(LoadStatus.REFRESH)
        }

        mAdapter.apply {
            mAdapter.addHeader(headerBinding.root)
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
            setOnItemClickListener { view, entity, position ->  }
        }
    }

    override fun bindData() {
        viewModel.bannerLive.observeState<List<Banner>>(this, success = {
            headerBinding.banner.bindDataCreate(it, object : CardImageCreateHolder<Banner>() {
                override fun bindItem(imageView: ImageView, data: Banner) {
                    GlideTools.get().loadImage(data.imagePath, imageView)
                }

                override fun onItemClick(position: Int, item: Banner) {
//                    findNavController().navigateDetails(KeyConstants.CONST_EMPTY, item.url)
                }

            })
        }, error = {
            toast(it.message)
        })

        viewModel.contentListLive.observeState<PageData<HomeContent>>(this, success = {
            binding.refreshView.isRefreshing = false
            mAdapter.submitData(it, diff = true)
        }, error = {
            toast(it.toString())
            binding.refreshView.isRefreshing = false
            mAdapter.submitFailed()
        })
    }

}