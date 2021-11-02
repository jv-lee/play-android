package com.lee.pioneer.library.common.entity

import androidx.annotation.Keep
import com.lee.library.adapter.page.PagingData

/**
 * @author jv.lee
 * @date 2020/3/25
 * @description 网络请求操作类
 */
@Keep
data class Data<T>(
    val data: T,
    val status: Int
)

@Keep
data class PageData<T>(
    val data: MutableList<T>,
    val status: Int = 0,
    val page: Int,
    val page_count: Int
) : PagingData<T> {
    override fun getPageNumber(): Int {
        return page
    }

    override fun getPageTotalNumber(): Int {
        return page_count
    }

    override fun getDataSource(): List<T> {
        return data
    }

}

/**
 * BannerAPI data<T>
 */
@Keep
data class Banner(
    val image: String,
    val title: String,
    val url: String
)

/**
 *  分类数据 data<T>
 */
@Keep
data class Category(
    val _id: String,
    val coverImageUrl: String,
    val desc: String,
    val title: String,
    val type: String
)

/**
 * 分页具体数据列表 data<T>
 * 最热数据列表 data<T>
 * 搜索数据列表 data<T>
 */
@Keep
data class Content(
    val _id: String,
    val author: String,
    val category: String,
    val createdAt: String,
    val desc: String,
    val images: List<String>,
    val likeCounts: Int,
    val publishedAt: String,
    val stars: Int,
    val title: String,
    val type: String,
    val url: String,
    val views: Int,
    var viewType: Int = 0
)