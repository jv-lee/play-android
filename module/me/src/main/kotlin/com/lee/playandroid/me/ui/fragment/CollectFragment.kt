package com.lee.playandroid.me.ui.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.adapter.listener.LoadErrorListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseNavigationAnimationFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.toast
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.observeState
import com.lee.library.utils.LogUtil
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentCollectBinding
import com.lee.playandroid.me.ui.adapter.SimpleTextAdapter
import com.lee.playandroid.me.viewmodel.CollectViewModel
import com.lee.playandroid.router.navigateDetails

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description 收藏列表页
 */
class CollectFragment : BaseNavigationAnimationFragment(R.layout.fragment_collect) {

    private val viewModel by viewModels<CollectViewModel>()

    private val binding by binding(FragmentCollectBinding::bind)

    private lateinit var mAdapter: SimpleTextAdapter

    override fun bindView() {
        binding.rvContainer.adapter = SimpleTextAdapter(requireContext(), arrayListOf()).apply {
            mAdapter = this
            initStatusView()
            pageLoading()

            setAutoLoadMoreListener {
                viewModel.requestCollect(LoadStatus.LOAD_MORE)
            }

            setLoadErrorListener(object : LoadErrorListener {
                override fun pageReload() {
                    viewModel.requestCollect(LoadStatus.REFRESH)
                }

                override fun itemReload() {
                    viewModel.requestCollect(LoadStatus.RELOAD)
                }
            })

            setOnItemClickListener { _, entity, _ ->
                findNavController().navigateDetails(entity.title, entity.link)
            }
        }.proxy
    }

    override fun bindData() {
        LogUtil.i("bindData")
        viewModel.collectLive.observeState<PageData<Content>>(this, success = {
            LogUtil.i("setData")
            mAdapter.submitData(it)
        }, error = {
            toast(it.message)
            mAdapter.submitFailed()
        })
    }

}