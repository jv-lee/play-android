package com.lee.playandroid.library.common.entity

import com.lee.library.adapter.page.PagingData

data class PageUiData<T>(val page: Int, val pageTotal: Int, val data: MutableList<T>) :
    PagingData<T> {
    override fun getPageNumber(): Int {
        return page
    }

    override fun getPageTotalNumber(): Int {
        return pageTotal
    }

    override fun getDataSource(): List<T> {
        return data
    }

}