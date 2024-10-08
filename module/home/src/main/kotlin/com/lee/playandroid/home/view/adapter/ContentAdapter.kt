package com.lee.playandroid.home.view.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.lee.playandroid.base.adapter.binding.ViewBindingAdapter
import com.lee.playandroid.base.adapter.binding.ViewBindingHolder
import com.lee.playandroid.base.adapter.item.ViewBindingItem
import com.lee.playandroid.base.widget.banner.holder.CardImageCreateHolder
import com.lee.playandroid.common.entity.Banner
import com.lee.playandroid.common.extensions.getAuthor
import com.lee.playandroid.common.extensions.getCategory
import com.lee.playandroid.common.extensions.getDateFormat
import com.lee.playandroid.common.extensions.getTitle
import com.lee.playandroid.common.tools.GlideTools
import com.lee.playandroid.home.bean.HomeContent
import com.lee.playandroid.home.databinding.ItemContentBannerBinding
import com.lee.playandroid.home.databinding.ItemContentCategoryBinding
import com.lee.playandroid.home.databinding.ItemContentTextBinding
import com.lee.playandroid.home.model.entity.HomeCategory
import com.lee.playandroid.router.NavigationAnim
import com.lee.playandroid.router.navigateDeepLink
import com.lee.playandroid.router.navigateDetails

/**
 * 首页Home数据列表适配器
 * @author jv.lee
 * @date 2021/11/2
 */
class ContentAdapter(context: Context) :
    ViewBindingAdapter<HomeContent>(context) {

    companion object {
        const val CONTENT_TEXT_ITEM_TYPE = 2
    }

    init {
        addItemStyles(ContentBannerItem())
        addItemStyles(ContentCategoryItem())
        // 当前类型需要做itemDecoration(LabelDecoration) 处理 ，根据当前添加下标作为类型 ，修改顺序需同步修改 @see CONTENT_TEXT_ITEM_TYPE = 2
        addItemStyles(ContentTextItem())
    }

    /**
     * 首页BANNER样式
     */
    inner class ContentBannerItem : ViewBindingItem<HomeContent>() {

        override fun isItemView(entity: HomeContent, position: Int): Boolean {
            return entity.bannerList != null
        }

        override fun convert(
            holder: ViewBindingHolder,
            entity: HomeContent,
            position: Int
        ) {
            entity.bannerList ?: return
            holder.getViewBinding<ItemContentBannerBinding>().apply {
                banner.bindDataCreate(entity.bannerList, object : CardImageCreateHolder<Banner>() {
                    override fun bindItem(imageView: ImageView, data: Banner) {
                        GlideTools.get().loadImage(data.imagePath, imageView)
                    }

                    override fun onItemClick(position: Int, item: Banner) {
                        item.apply {
                            Navigation.findNavController(banner)
                                .navigateDetails(title, url, id, collect)
                        }
                    }
                }
                )
            }
        }

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemContentBannerBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        }
    }

    /**
     * 首页分类样式
     */
    inner class ContentCategoryItem : ViewBindingItem<HomeContent>() {

        private var mAdapter: ContentCategoryAdapter? = null

        override fun isItemView(entity: HomeContent, position: Int): Boolean {
            return entity.categoryList != null
        }

        override fun convert(
            holder: ViewBindingHolder,
            entity: HomeContent,
            position: Int
        ) {
            val context = holder.itemView.context
            val data = entity.categoryList
            data ?: return

            // 构建适配器
            mAdapter ?: kotlin.run {
                mAdapter = ContentCategoryAdapter(context).apply {
                    addData(data)
                    setOnItemClickListener(object : OnItemClickListener<HomeCategory> {
                        override fun onItemClick(
                            view: View,
                            entity: HomeCategory,
                            position: Int
                        ) {
                            Navigation.findNavController(view)
                                .navigateDeepLink(Uri.parse(entity.link), NavigationAnim.ZoomIn)
                        }

                    })
                }
            }

            holder.getViewBinding<ItemContentCategoryBinding>().apply {
                // 设置列表基础参数
                rvContainer.layoutManager ?: kotlin.run {
                    rvContainer.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                }
                rvContainer.adapter ?: kotlin.run {
                    rvContainer.adapter = mAdapter
                }
            }
        }

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemContentCategoryBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        }
    }

    /**
     * 首页普通信息条目
     */
    inner class ContentTextItem : ViewBindingItem<HomeContent>() {

        override fun isItemView(entity: HomeContent, position: Int): Boolean {
            return entity.content != null
        }

        override fun convert(
            holder: ViewBindingHolder,
            entity: HomeContent,
            position: Int
        ) {
            holder.getViewBinding<ItemContentTextBinding>().apply {
                entity.content?.apply {
                    tvTitle.text = getTitle()
                    tvAuthor.text = getAuthor()
                    tvTime.text = getDateFormat()
                    tvCategory.text = getCategory()
                }
            }
        }

        override fun getItemViewBinding(context: Context, parent: ViewGroup): ViewBinding {
            return ItemContentTextBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        }
    }
}