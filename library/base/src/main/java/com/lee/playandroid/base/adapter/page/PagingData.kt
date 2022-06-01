package com.lee.playandroid.base.adapter.page

/**
 *
 * @author jv.lee
 * @date 2020/8/11
 */
interface PagingData<T> {
    fun getPageNumber():Int
    fun getPageTotalNumber():Int
    fun getDataSource(): MutableList<T>
}