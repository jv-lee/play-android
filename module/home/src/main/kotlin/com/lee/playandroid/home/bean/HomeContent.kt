package com.lee.playandroid.home.bean

import com.lee.playandroid.library.common.entity.Banner
import com.lee.playandroid.library.common.entity.Content
import com.lee.playandroid.home.model.entity.HomeCategory

/**
 * 首页多类型Ui数据实体
 * @see com.lee.playandroid.home.view.adapter.ContentAdapter
 * @author jv.lee
 * @date 2021/11/4
 */
data class HomeContent(
    val bannerList: List<Banner>? = null,
    val categoryList: List<HomeCategory>? = null,
    val content: Content? = null
)