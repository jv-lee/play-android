package com.lee.playandroid.details

import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import com.just.agentweb.AgentWeb
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.arguments
import com.lee.library.extensions.binding
import com.lee.playandroid.details.databinding.FragmentContentDetailsBinding
import com.lee.playandroid.library.common.constant.KeyConstants
import com.lee.playandroid.library.common.extensions.bindBack
import com.lee.playandroid.library.common.extensions.bindLifecycle

/**
 * @author jv.lee
 * @date 2020/3/24
 * @description 内容详情页
 */
class ContentDetailsFragment :
    BaseFragment(R.layout.fragment_content_details) {

    private val detailsUrl by arguments<String>(KeyConstants.ARG_PARAMS_URL)

    private val binding by binding(FragmentContentDetailsBinding::bind)

    private lateinit var web: AgentWeb

    override fun bindView() {
        web = AgentWeb.with(this)
            .setAgentWebParent(binding.frameContainer, FrameLayout.LayoutParams(-1, -1))
            .useDefaultIndicator(R.color.colorThemeAccent)
            .createAgentWeb()
            .ready()
            .go(detailsUrl)
            .bindLifecycle(lifecycle)
            .bindBack(requireActivity().onBackPressedDispatcher) {
                findNavController().popBackStack()
            }
    }

    override fun bindData() {
    }

}
