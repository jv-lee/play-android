package com.lee.playandroid.search.model.entity

/**
 * 搜索热词 提供本地数据源
 * @author jv.lee
 * @date 2021/11/4
 */
data class SearchHot(val key: String){
    companion object{
        /**
         * 提供热门搜索词条
         */
        fun getHotCategory() = arrayListOf(
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
}