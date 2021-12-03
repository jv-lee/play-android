package com.lee.playandroid.me.ui.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.lee.library.adapter.listener.LoadErrorListener
import com.lee.library.adapter.page.submitData
import com.lee.library.adapter.page.submitFailed
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.toast
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.observeState
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.entity.PageData
import com.lee.playandroid.me.R
import com.lee.playandroid.me.databinding.FragmentCollectBinding
import com.lee.playandroid.me.ui.adapter.SimpleTextAdapter
import com.lee.playandroid.me.ui.widget.RecyclerItemTouchHelper
import com.lee.playandroid.me.viewmodel.CollectViewModel
import com.lee.playandroid.router.navigateDetails
import java.util.*

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description 收藏列表页
 */
class CollectFragment : BaseFragment(R.layout.fragment_collect),
    RecyclerItemTouchHelper.ItemTouchHelperCallback {

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
                findNavController()
                    .navigateDetails(entity.id, entity.title, entity.link, entity.collect)
            }
        }.proxy

        //绑定拖动滑动处理
        val callback = RecyclerItemTouchHelper(this)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvContainer)
    }

    override fun bindData() {
        viewModel.collectLive.observeState<PageData<Content>>(this, success = {
            mAdapter.submitData(it)
        }, error = {
            toast(it.message)
            mAdapter.submitFailed()
        })
    }

    override fun onItemDelete(position: Int) {
        //数据删除
        mAdapter.data.removeAt(position)
        mAdapter.notifyItemRemoved(position)

        //viewModel数据源处理
        viewModel.collectLive.getValueData<PageData<Content>>()?.apply {
            data.removeAt(position)
        }
    }

    override fun onMove(fromPosition: Int, toPosition: Int) {
        //数据交换
        Collections.swap(mAdapter.data, fromPosition, toPosition)
        mAdapter.notifyItemMoved(fromPosition, toPosition)

        //viewModel数据源处理
        viewModel.collectLive.getValueData<PageData<Content>>()?.apply {
            Collections.swap(data, fromPosition, toPosition)
        }
    }

}