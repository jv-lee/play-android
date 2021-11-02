package com.lee.pioneer.library.common.constant

/**
 * @author jv.lee
 * @date 2020/4/15
 * @description
 */
interface CacheConstants {
    companion object {
        const val CONTENT_CACHE_KEY = "content-cache-key"//首页 contentList数据列表第一页缓存
        const val CATEGORY_CACHE_KEY = "category-cache-key"//首页 分类tab缓存
        const val RECOMMEND_CACHE_KEY = "recommend-cache-key"//推荐页 数据缓存
        const val RECOMMEND_BANNER_KEY =  "recommend-banner-cache-key"//推荐页banner缓存
    }
}