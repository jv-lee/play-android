package com.lee.playandroid.base.adapter.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lee.playandroid.base.adapter.base.BaseViewHolder
import com.lee.playandroid.base.adapter.base.BaseViewItem
import com.lee.playandroid.base.adapter.binding.ViewBindingHolder
import com.lee.playandroid.base.tools.ViewBindingTools

/**
 * viewBinding实现 item类型类
 * @author jv.lee
 * @date 2021/6/15
 */
abstract class ViewBindingItem<VB : ViewBinding, Data> : BaseViewItem<Data> {

    private var _binding: VB? = null
    val mBinding: VB get() = _binding!!

    override fun getItemViewAny(context: Context, parent: ViewGroup): Any {
        _binding = ViewBindingTools.inflateWithGeneric(
            this,
            LayoutInflater.from(context), parent, false
        )
        return mBinding
    }

    override fun convert(holder: BaseViewHolder, entity: Data, position: Int) {
        mBinding.convert(holder as ViewBindingHolder, entity, position)
    }

    override fun viewRecycled(holder: BaseViewHolder) {
        mBinding.viewRecycled(holder as ViewBindingHolder)
    }

    abstract fun VB.convert(holder: ViewBindingHolder, entity: Data, position: Int)

    open fun VB.viewRecycled(holder: ViewBindingHolder) {}

    // Deprecated use ViewBindingUtil.inflateWithGeneric binding
    // impl ItemBinding.inflate(LayoutInflater.from(context), parent, false)
//    abstract fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding
}