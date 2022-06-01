package com.lee.playandroid.base.adapter.page

/**
 *
 * @author jv.lee
 * @date 2020/8/11
 */
interface PagingData2<T> {
    fun isFirstPage(): Boolean
    fun isLastPage(): Boolean
    fun getDataSource(): List<T>
}