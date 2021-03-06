package com.lee.playandroid.home.model.entity

import com.lee.playandroid.home.R

/**
 * 首页分类本地数据帮助类
 * @author jv.lee
 * @date 2021/11/4
 */
data class HomeCategory(val iconResId: Int, val name: String, val link: String) {
    companion object {
        /**
         * 提供本地首页分类数据
         */
        fun getHomeCategory() = arrayListOf(
            HomeCategory(
                R.drawable.vector_icon_official,
                "公众号",
                "play://official"
            ),
            HomeCategory(
                R.drawable.vector_icon_project,
                "项目",
                "play://project"
            )
        )
    }
}