package com.lee.playandroid.details

import android.widget.FrameLayout
import com.just.agentweb.AgentWeb
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.arguments
import com.lee.library.extensions.binding
import com.lee.library.extensions.setWebBackEvent
import com.lee.playandroid.details.databinding.FragmentDetailsBinding
import com.lee.playandroid.library.common.extensions.bindLifecycle

private const val ARG_PARAMS_TITLE = "title"
private const val ARG_PARAMS_URL = "url"

/**
 * @author jv.lee
 * @date 2020/3/24
 * @description 内容详情页
 */
class DetailsFragment :
    BaseFragment(R.layout.fragment_details) {

    private val title by arguments<String>(ARG_PARAMS_TITLE)
    private val detailsUrl by arguments<String>(ARG_PARAMS_URL)

    private val binding by binding(FragmentDetailsBinding::bind)

    private lateinit var web: AgentWeb

    override fun bindView() {
        binding.toolbar.setTitleText(title)

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
    }

}
