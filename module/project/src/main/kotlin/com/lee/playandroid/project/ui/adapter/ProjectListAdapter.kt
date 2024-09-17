package com.lee.playandroid.project.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.shape.ShapeAppearanceModel
import com.lee.playandroid.base.adapter.binding.ViewBindingAdapter
import com.lee.playandroid.base.adapter.binding.ViewBindingHolder
import com.lee.playandroid.base.adapter.item.ViewBindingItem
import com.lee.playandroid.common.entity.Content
import com.lee.playandroid.common.extensions.getAuthor
import com.lee.playandroid.common.extensions.getDateFormat
import com.lee.playandroid.common.extensions.getTitle
import com.lee.playandroid.common.tools.GlideTools
import com.lee.playandroid.project.R
import com.lee.playandroid.project.databinding.ItemProjectBinding

/**
 * 项目列表适配器
 * @author jv.lee
 * @date 2021/11/8
 */
class ProjectListAdapter(context: Context) :
    ViewBindingAdapter<Content>(context) {

    init {
        addItemStyles(ProjectItem())
    }

    inner class ProjectItem : ViewBindingItem<Content>() {

        override fun convert(
            holder: ViewBindingHolder,
            entity: Content,
            position: Int
        ) {
            holder.getViewBinding<ItemProjectBinding>().apply {

                entity.apply {
                    root.context.resources.apply {
                        ivImage.shapeAppearanceModel = ShapeAppearanceModel.Builder()
                            .setTopLeftCornerSize(getDimension(R.dimen.offset_radius_medium))
                            .setBottomLeftCornerSize(getDimension(R.dimen.offset_radius_medium))
                            .build()
                    }

                    GlideTools.get().loadImage(envelopePic, ivImage)

                    tvTitle.text = getTitle()
                    tvDescription.text = desc
                    tvAuthor.text = getAuthor()
                    tvTime.text = getDateFormat()
                }
            }
        }

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemProjectBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        }
    }
}