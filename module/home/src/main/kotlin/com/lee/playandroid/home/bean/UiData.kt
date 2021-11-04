package com.lee.playandroid.home.bean

import com.lee.pioneer.library.common.entity.Content
import com.lee.playandroid.home.helper.HomeCategory

/**
 * @author jv.lee
 * @date 2021/11/4
 * @description
 */
data class HomeContent(val content: Content? = null, val categoryList: List<HomeCategory>? = null)