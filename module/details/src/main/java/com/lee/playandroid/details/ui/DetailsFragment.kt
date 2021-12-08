package com.lee.playandroid.details.ui

import android.view.View
import android.widget.FrameLayout
import com.just.agentweb.AgentWeb
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.*
import com.lee.library.utils.ShareUtil
import com.lee.library.widget.toolbar.TitleToolbar
import com.lee.playandroid.details.R
import com.lee.playandroid.details.databinding.FragmentDetailsBinding
import com.lee.playandroid.details.viewmodel.DetailsViewModel
import com.lee.playandroid.library.common.extensions.bindLifecycle

/**
 * @author jv.lee
 * @date 2020/3/24
 * @description 内容详情页
 */
class DetailsFragment : BaseFragment(R.layout.fragment_details) {

    companion object {
        const val ARG_PARAMS_ID = "id"
        const val ARG_PARAMS_TITLE = "title"
        const val ARG_PARAMS_URL = "url"
        const val ARG_PARAMS_COLLECT = "isCollect"
    }

    private val title by arguments<String>(ARG_PARAMS_TITLE)
    private val detailsUrl by arguments<String>(ARG_PARAMS_URL)

    private val viewModel by viewModelByFactory<DetailsViewModel>()

    private val binding by binding(FragmentDetailsBinding::bind)

    private lateinit var web: AgentWeb

    private val toolbarClickListener = object : TitleToolbar.ClickListener() {
        override fun menuClick() {
            binding.toolbar.showMenu(-40, 10)
        }

        override fun menuItemClick(view: View) {
            when (view.id) {
                R.id.collect -> {
                    viewModel.requestCollect()
                }
                R.id.share -> {
                    ShareUtil.shareText(context, "$title:$detailsUrl")
                }
            }
        }
    }

    override fun bindView() {
        binding.toolbar.setTitleText(title)
        binding.toolbar.setClickListener(toolbarClickListener)

        web = AgentWeb.with(this)
            .setAgentWebParent(binding.frameContainer, FrameLayout.LayoutParams(-1, -1))
            .useDefaultIndicator(R.color.colorThemeAccent)
            .createAgentWeb()
            .ready()
            .go(detailsUrl)
            .bindLifecycle(lifecycle)
            .apply { webCreator.webView.setWebBackEvent() }
    }

    override fun bindData() {
        viewModel.collectLive.observe(this, { isCollect ->
            toast(getString(if (isCollect) R.string.menu_collect_complete else R.string.menu_collect_completed))
        })

        viewModel.failedEvent.observe(this) { error ->
            toast(error.message)
        }
    }

}