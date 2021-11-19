package com.lee.playandroid.search.helper

/**
 * @author jv.lee
 * @data 2021/11/4
 * @description 热门搜索词条数据帮助类
 */
object SearchHelper {

    /**
     * 提供热门搜索词条
     */
    fun getHomeCategory() = arrayListOf(
        SearchHot("MVVM"),
        SearchHot("面试"),
        SearchHot("gradle"),
        SearchHot("动画"),
        SearchHot("CameraX"),
        SearchHot("自定义View"),
        SearchHot("性能优化"),
        SearchHot("Jetpack"),
        SearchHot("Kotlin"),
        SearchHot("Flutter"),
        SearchHot("OpenGL"),
        SearchHot("FFmpeg")
    )
}

data class SearchHot(val key: String)