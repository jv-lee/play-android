/*
 * 分页数据包装实体
 */
package com.lee.playandroid.common.entity

import com.lee.playandroid.base.adapter.page.PagingData

data class PageUiData<T>(val page: Int, val pageTotal: Int, val data: MutableList<T>) :
    PagingData<T> {
    override fun getPageNumber(): Int {
        return page
    }

    override fun getPageTotalNumber(): Int {
        return pageTotal
    }

    override fun getDataSource(): MutableList<T> {
        return data
    }
}