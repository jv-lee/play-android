package com.lee.playandroid.home.helper

import com.lee.playandroid.home.R

/**
 * @author jv.lee
 * @data 2021/11/4
 * @description
 */
object CategoryHelper {
    fun getHomeCategory() = arrayListOf(
        HomeCategory(R.mipmap.ic_splash_info, "公众号", ""),
        HomeCategory(R.mipmap.ic_splash_info, "项目", "")
    )
}

data class HomeCategory(val icon: Int, val name: String, val link: String)