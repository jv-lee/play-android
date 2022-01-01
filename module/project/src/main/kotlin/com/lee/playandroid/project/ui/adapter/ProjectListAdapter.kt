package com.lee.playandroid.project.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.viewbinding.ViewBinding
import com.google.android.material.shape.ShapeAppearanceModel
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.library.base.ApplicationExtensions.app
import com.lee.library.utils.TimeUtil
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.library.common.tools.GlideTools
import com.lee.playandroid.project.R
import com.lee.playandroid.project.databinding.ItemProjectBinding

/**
 * @author jv.lee
 * @date 2021/11/8
 * @description 项目列表适配器
 */
class ProjectListAdapter(context: Context, data: List<Content>) :
    ViewBindingAdapter<Content>(context, data) {

    init {
        addItemStyles(ProjectItem())
    }

    inner class ProjectItem : ViewBindingItem<Content>() {
        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemProjectBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun convert(holder: ViewBindingHolder, entity: Content, position: Int) {
            holder.getViewBinding<ItemProjectBinding>().apply {
                entity.apply {
                    ivImage.shapeAppearanceModel = ShapeAppearanceModel.Builder()
                        .setTopLeftCornerSize(root.context.resources.getDimension(R.dimen.radius_offset))
                        .setBottomLeftCornerSize(root.context.resources.getDimension(R.dimen.radius_offset))
                        .build()
                    GlideTools.get().loadImage(envelopePic, ivImage)

                    tvTitle.text = HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    tvDescription.text = desc
                    tvAuthor.text = if (author.isEmpty()) shareUser else author
                    tvTime.text = TimeUtil.getChineseTimeMill(publishTime)
                }
            }
        }

    }

}