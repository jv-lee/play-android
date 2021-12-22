package com.lee.playandroid.home.view.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.text.HtmlCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.lee.library.adapter.binding.ViewBindingAdapter
import com.lee.library.adapter.binding.ViewBindingHolder
import com.lee.library.adapter.item.ViewBindingItem
import com.lee.library.utils.TimeUtil
import com.lee.library.widget.banner.holder.CardImageCreateHolder
import com.lee.playandroid.home.bean.HomeContent
import com.lee.playandroid.home.databinding.ItemContentBannerBinding
import com.lee.playandroid.home.databinding.ItemContentCategoryBinding
import com.lee.playandroid.home.databinding.ItemContentTextBinding
import com.lee.playandroid.library.common.entity.Banner
import com.lee.playandroid.library.common.tools.GlideTools
import com.lee.playandroid.router.extensions.NavigationAnim
import com.lee.playandroid.router.extensions.navigateDeepLink
import com.lee.playandroid.router.navigateDetails

/**
 * @author jv.lee
 * @date 2021/11/2
 * @description 首页Home数据列表适配器
 */
class ContentAdapter(context: Context, data: List<HomeContent>) :
    ViewBindingAdapter<HomeContent>(context, data) {

    companion object {
        const val CONTENT_TEXT_ITEM_TYPE = 2
    }

    init {
        addItemStyles(ContentBannerItem())
        addItemStyles(ContentCategoryItem())
        //当前类型需要做itemDecoration(LabelDecoration) 处理 ，根据当前添加下标作为类型 ，修改顺序需同步修改 @see CONTENT_TEXT_ITEM_TYPE = 2
        addItemStyles(ContentTextItem())
    }

    /**
     * 首页BANNER样式
     */
    inner class ContentBannerItem : ViewBindingItem<HomeContent>() {
        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemContentBannerBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun isItemView(entity: HomeContent, position: Int): Boolean {
            return entity.bannerList != null
        }

        override fun convert(holder: ViewBindingHolder, entity: HomeContent, position: Int) {
            holder.getViewBinding<ItemContentBannerBinding>().apply {
                entity.bannerList?.apply {
                    banner.bindDataCreate(this, object : CardImageCreateHolder<Banner>() {
                        override fun bindItem(imageView: ImageView, data: Banner) {
                            GlideTools.get().loadImage(data.imagePath, imageView)
                        }

                        override fun onItemClick(position: Int, item: Banner) {
                            item.apply {
                                Navigation.findNavController(banner)
                                    .navigateDetails(title, url, id, collect)
                            }
                        }
                    })
                }
            }
        }

    }

    /**
     * 首页分类样式
     */
    inner class ContentCategoryItem : ViewBindingItem<HomeContent>() {

        private var mAdapter: ContentCategoryAdapter? = null

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemContentCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun isItemView(entity: HomeContent, position: Int): Boolean {
            return entity.categoryList != null
        }

        override fun convert(holder: ViewBindingHolder, entity: HomeContent, position: Int) {
            holder.getViewBinding<ItemContentCategoryBinding>().apply {
                val context = holder.itemView.context
                val data = entity.categoryList
                data ?: return

                //构建适配器
                mAdapter ?: kotlin.run {
                    mAdapter = ContentCategoryAdapter(context, data).apply {
                        setOnItemClickListener { view, entity, _ ->
                            Navigation.findNavController(view)
                                .navigateDeepLink(Uri.parse(entity.link), NavigationAnim.ZoomIn)
                        }
                    }
                }

                //设置列表基础参数
                rvContainer.layoutManager ?: kotlin.run {
                    rvContainer.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                }
                rvContainer.adapter ?: kotlin.run {
                    rvContainer.adapter = mAdapter
                }
            }
        }

    }

    /**
     * 首页普通信息条目
     */
    inner class ContentTextItem : ViewBindingItem<HomeContent>() {

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemContentTextBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun isItemView(entity: HomeContent, position: Int): Boolean {
            return entity.content != null
        }

        override fun convert(holder: ViewBindingHolder, entity: HomeContent, position: Int) {
            holder.getViewBinding<ItemContentTextBinding>().apply {

                entity.content?.apply {
                    tvTitle.text = HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    tvAuthor.text = if (author.isEmpty()) shareUser else author
                    tvTime.text = TimeUtil.getChineseTimeMill(publishTime)

                    tvCategory.text = when {
                        superChapterName.isNotEmpty() and chapterName.isNotEmpty() -> "$superChapterName / $chapterName"
                        superChapterName.isNotEmpty() -> superChapterName
                        chapterName.isNotEmpty() -> chapterName
                        else -> ""
                    }
                }

            }
        }

    }

}