package com.lee.playandroid.base.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.playandroid.base.tools.ViewBindingTools

/**
 * 通过反射实现binding注入 baseDialogFragment
 * @author jv.lee
 * @date 2024/6/6
 */
abstract class BaseBindingDialogFragment<VB : ViewBinding>(
    isCancel: Boolean = true,
    isFullWindow: Boolean = true
) :
    BaseDialogFragment(isCancel = isCancel, isFullWindow = isFullWindow) {

    private var _binding: VB? = null
    val mBinding: VB get() = _binding!!
    open val contentView: View get() = mBinding.root

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ViewBindingTools.inflateWithGeneric(this, inflater, container, false)
        return contentView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}