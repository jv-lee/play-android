package com.lee.playandroid.library.common.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.lee.library.adapter.page.PagingData
import kotlinx.parcelize.Parcelize

/**
 * @author jv.lee
 * @date 2020/3/25
 * @description 网络请求操作类
 */
@Keep
data class Data<T>(
    val data: T,
    val errorCode: Int,
    val errorMsg: String
)

@Keep
data class PageData<T>(
    @SerializedName("datas")
    val data: MutableList<T>,
    val curPage: Int,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int,
) : PagingData<T> {
    override fun getPageNumber(): Int {
        return curPage
    }

    override fun getPageTotalNumber(): Int {
        return pageCount
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
    val id: String,
    val title: String,
    val desc: String,
    val imagePath: String,
    val url: String,
    val isVisible: Int,
    val order: Int,
    val type: Int,
)

/**
 * 分页具体数据列表 data<T>
 * 最热数据列表 data<T>
 * 搜索数据列表 data<T>
 */
@Keep
data class Content(
    val apkLink: String,
    val audit: Int,
    val author: String,
    val chapterId: Int,
    val chapterName: String,
    val collect: Boolean,
    val courseId: Int,
    val desc: String,
    val envelopePic: String,
    val fresh: Boolean,
    val id: Long,
    val link: String,
    val niceDate: String,
    val niceShareDate: String,
    val origin: String,
    val prefix: String,
    val projectLink: String,
    val publishTime: Long,
    val shareDate: Long,
    val shareUser: String,
    val superChapterId: Long,
    val superChapterName: String,
    val tags: List<Tag>,
    val title: String,
    val type: Int,
    val userId: Long,
    val visible: Int,
    val zan: Long,
    val top: String
)

@Keep
data class Tag(
    val name: String,
    val url: String
)

@Parcelize
@Keep
data class Tab(
    val id: Long,
    val courseId: Long,
    val name: String,
    val order: Long,
    val parentChapterId: Long,
    val userControlSetTop: Boolean,
    val visible: Int
) : Parcelable

@Keep
data class ParentTab(
    val id: Long,
    val courseId: Long,
    val name: String,
    val order: Long,
    val parentChapterId: Long,
    val userControlSetTop: Boolean,
    val visible: Int,
    val children: List<Tab>
)

@Keep
data class NavigationItem(
    val cid: Long,
    val name: String,
    val articles:List<Content>
)