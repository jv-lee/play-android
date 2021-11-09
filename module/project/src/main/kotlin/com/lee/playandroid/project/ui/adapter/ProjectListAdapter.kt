package com.lee.playandroid.project.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.library.utils.TimeUtil
import com.lee.pioneer.library.common.entity.Content
import com.lee.pioneer.library.common.tools.GlideTools
import com.lee.playandroid.project.databinding.ItemProjectBinding
import com.lee.playandroid.project.databinding.ItemProjectSimpleBinding

/**
 * @author jv.lee
 * @data 2021/11/8
 * @description
 */
class ProjectListAdapter(context: Context, data: List<Content>) :
    ViewBindingAdapter<Content>(context, data) {

    init {
        addItemStyles(ProjectItem())
//        addItemStyles(ProjectSimpleItem())
    }

    inner class ProjectItem : ViewBindingItem<Content>() {
        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemProjectBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun convert(holder: ViewBindingHolder, entity: Content, position: Int) {
            holder.getViewBinding<ItemProjectBinding>().apply {
                entity.apply {
                    GlideTools.get().loadImage(envelopePic, ivImage)

                    tvTitle.text = HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    tvDescription.text = desc
                    tvAuthor.text = if (author.isEmpty()) shareUser else author
                    tvTime.text = TimeUtil.getChineseTimeMill(publishTime)
                }
            }
        }

    }

    inner class ProjectSimpleItem : ViewBindingItem<Content>() {
        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemProjectSimpleBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun convert(holder: ViewBindingHolder, entity: Content, position: Int) {
            holder.getViewBinding<ItemProjectSimpleBinding>().apply {
                entity.apply {
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