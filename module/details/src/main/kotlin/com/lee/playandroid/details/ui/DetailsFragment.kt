package com.lee.playandroid.details.ui

import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import com.just.agentweb.AgentWeb
import com.lee.playandroid.base.base.BaseNavigationFragment
import com.lee.playandroid.base.dialog.LoadingDialog
import com.lee.playandroid.base.extensions.*
import com.lee.playandroid.base.interadp.setClickListener
import com.lee.playandroid.base.tools.DarkModeTools
import com.lee.playandroid.base.utils.ShareUtil
import com.lee.playandroid.base.viewstate.collectState
import com.lee.playandroid.common.extensions.bindLifecycle
import com.lee.playandroid.common.extensions.supportDarkMode
import com.lee.playandroid.details.R
import com.lee.playandroid.details.databinding.FragmentDetailsBinding
import com.lee.playandroid.details.viewmodel.DetailsViewAction
import com.lee.playandroid.details.viewmodel.DetailsViewEvent
import com.lee.playandroid.details.viewmodel.DetailsViewModel
import com.lee.playandroid.details.viewmodel.DetailsViewState

/**
 * 文章详情页
 * @author jv.lee
 * @date 2020/3/24
 */
class DetailsFragment : BaseNavigationFragment(R.layout.fragment_details) {

    companion object {
        const val ARG_PARAMS_ID = "id"
        const val ARG_PARAMS_TITLE = "title"
        const val ARG_PARAMS_URL = "url"
        const val ARG_PARAMS_COLLECT = "isCollect"
    }

    private val url by arguments<String>(ARG_PARAMS_URL)

    private val viewModel by viewModels<DetailsViewModel>()

    private val binding by binding(FragmentDetailsBinding::bind)

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    private lateinit var web: AgentWeb

    override fun bindView() {
        binding.toolbar.setClickListener {
            moreClick { binding.toolbar.showMenu(-40, 10) }
            menuItemClick { view ->
                when (view.id) {
                    R.id.collect -> {
                        viewModel.dispatch(DetailsViewAction.UpdateCollectStatus)
                    }
                    R.id.share -> {
                        viewModel.dispatch(DetailsViewAction.RequestShareDetails)
                    }
                }
            }
        }

        web = AgentWeb.with(this)
            .setAgentWebParent(binding.frameContainer, FrameLayout.LayoutParams(-1, -1))
            .useDefaultIndicator(ContextCompat.getColor(requireContext(), R.color.colorThemeAccent))
            .createAgentWeb()
            .ready()
            .go(url)
            .bindLifecycle(lifecycle)
            .apply {
                webCreator.webView.setWebBackEvent()
                DarkModeTools.get().changeDarkModeState()
                supportDarkMode()
            }
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchWhenResumed {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is DetailsViewEvent.CollectEvent -> {
                        toast(event.message)
                    }
                    is DetailsViewEvent.ShareEvent -> {
                        ShareUtil.shareText(requireContext(), event.shareText)
                    }
                }
            }
        }

        launchWhenResumed {
            viewModel.viewStates.collectState(
                DetailsViewState::title,
                DetailsViewState::actionEnable
            ) { title, enable ->
                binding.toolbar.setTitleText(title)
                binding.toolbar.setMoreVisible(enable)
            }
        }

        launchWhenResumed {
            viewModel.viewStates.collectState(DetailsViewState::isLoading) {
                if (it) show(loadingDialog) else dismiss(loadingDialog)
            }
        }
    }
}