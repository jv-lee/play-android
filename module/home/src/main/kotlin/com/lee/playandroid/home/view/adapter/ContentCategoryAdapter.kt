package com.lee.playandroid.home.view.adapter

import android.content.Context
import com.lee.playandroid.base.adapter.binding.ViewBindingAdapter
import com.lee.playandroid.base.adapter.binding.ViewBindingHolder
import com.lee.playandroid.base.adapter.item.ViewBindingItem
import com.lee.playandroid.base.extensions.setImageTintCompat
import com.lee.playandroid.home.R
import com.lee.playandroid.home.databinding.ItemContentCategoryChildBinding
import com.lee.playandroid.home.model.entity.HomeCategory

/**
 * 首页分类列表适配器 基于
 * @see ContentAdapter.ContentCategoryItem
 * @author jv.lee
 * @date 2021/11/8
 */
class ContentCategoryAdapter(context: Context) :
    ViewBindingAdapter<HomeCategory>(context) {

    init {
        addItemStyles(ContentCategoryChildItem())
    }

    /**
     * 每条分类item样式
     */
    inner class ContentCategoryChildItem :
        ViewBindingItem<ItemContentCategoryChildBinding, HomeCategory>() {

        override fun ItemContentCategoryChildBinding.convert(
            holder: ViewBindingHolder,
            entity: HomeCategory,
            position: Int
        ) {
            // 重新设置item宽度,宽度占屏幕一半并且减去margin值
            val resource = root.context.resources
            val layoutParams = root.layoutParams
            val marginOffset = resource.getDimension(R.dimen.offset_small)
            layoutParams.width =
                ((resource.displayMetrics.widthPixels / 2) - (marginOffset * 2)).toInt()
            root.layoutParams = layoutParams

            // 设置内容信息
            ivCategoryIcon.setImageTintCompat(entity.iconResId)
            tvCategoryName.text = entity.name
        }
    }
}