package com.lee.playandroid.details

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.lee.library.base.BaseVMNavigationFragment
import com.lee.library.extensions.arguments
import com.lee.library.extensions.setWebBackEvent
import com.lee.library.mvvm.base.BaseViewModel
import com.lee.library.tools.WebViewTools
import com.lee.library.widget.AppWebView
import com.lee.pioneer.library.common.constant.KeyConstants
import com.lee.playandroid.details.databinding.FragmentContentDetailsBinding

/**
 * @author jv.lee
 * @date 2020/3/24
 * @description 内容详情页
 */
class ContentDetailsFragment :
    BaseVMNavigationFragment<FragmentContentDetailsBinding, BaseViewModel>(R.layout.fragment_content_details) {

    private val detailsUrl by arguments<String>(KeyConstants.KEY_URL)
    private val web by lazy { WebViewTools.getWeb(requireActivity().applicationContext) }

    override fun bindView() {
        web?.run {
            bindLifecycle(requireActivity() as LifecycleOwner)
            parent?.let { (it as ViewGroup).removeAllViews() }
            binding.frameContainer.addView(this)
            setWebBackEvent()
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true

            addWebStatusListenerAdapter(object : AppWebView.WebStatusListenerAdapter() {

                override fun callProgress(progress: Int) {
                    binding.progress.visibility = View.VISIBLE
                    binding.progress.progress = progress
                }

                override fun callSuccess() {
                    binding.progress.visibility = View.GONE
                }

                override fun callFailed() {
                    binding.progress.visibility = View.GONE
                }
            })
        }
    }

    override fun bindData() {
        web?.initUrl(detailsUrl)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        web?.destroyView()
    }

}
