package com.lee.playandroid.home.bean

import com.lee.pioneer.library.common.entity.Banner
import com.lee.pioneer.library.common.entity.Content
import com.lee.playandroid.home.helper.HomeCategory

/**
 * @author jv.lee
 * @date 2021/11/4
 * @description 首页多类型Ui数据实体
 * @see com.lee.playandroid.home.view.adapter.ContentAdapter
 */
data class HomeContent(
    val bannerList: List<Banner>? = null,
    val categoryList: List<HomeCategory>? = null,
    val content: Content? = null
)