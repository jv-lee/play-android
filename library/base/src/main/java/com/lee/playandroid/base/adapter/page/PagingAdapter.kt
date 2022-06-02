package com.lee.playandroid.base.adapter.page

import androidx.recyclerview.widget.DiffUtil
import com.lee.playandroid.base.adapter.base.BaseViewAdapter
import com.lee.playandroid.base.adapter.callback.DiffCallback

/**
 * 适配器加载数据状态逻辑
 * @author jv.lee
 * @date 2020/8/11
 */
fun <T> BaseViewAdapter<T>.submitData(
    pageData: PagingData<T>,
    limit: Int = 1,
    diff: Boolean = false,
    refreshBlock: () -> Unit = {},
    emptyBlock: () -> Unit = {}
) {
    //首页加载逻辑
    if (pageData.getPageNumber() == limit) {
        openLoadMore()

        // 过滤首页重复数据
        if (data == pageData.getDataSource()) {
            // 重复数据空数据校验
            if (pageData.getDataSource().isNullOrEmpty()) {
                if (isPageCompleted) initStatusView()
                clearData()
                pageEmpty()
                emptyBlock()
                return
            }

            // 刷新后首页数据即是末尾页
            if (pageData.getPageNumber() >= pageData.getPageTotalNumber()) {
                loadMoreEnd()
                return
            }

            // 过滤重复数据
            return
        }

        //设置空页面
        if (pageData.getDataSource().isNullOrEmpty()) {
            if (isPageCompleted) initStatusView()
            clearData()
            pageEmpty()
            emptyBlock()
            return
        }

        //数据源不同替换数据更改状态
        if (data != pageData.getDataSource()) {
            //正常情况第一页加载数据状态
            updateData(pageData.getDataSource())
            pageCompleted()
            refreshBlock()
        }

        //分页加载逻辑
    } else {

        // 数据相同不处理
        if (data == pageData.getDataSource()) {
            return
        }

        //防止view重构后在分页加载时 pageCompleted状态重置
        if (!isPageCompleted) {
            pageCompleted()
        }

        if (diff) {
            //防止activity重建在viewModel中填充历史数据 做差分填充
            val oldData = data
            updateData(pageData.getDataSource())
            val result =
                DiffUtil.calculateDiff(DiffCallback<T>(oldData, pageData.getDataSource()), true)
            result.dispatchUpdatesTo(this)
        } else {
            addData(pageData.getDataSource())
        }

    }

    //设置尾页状态 (包括notifyDateSetChange)
    if (pageData.getPageNumber() >= pageData.getPageTotalNumber()) {
        loadMoreEnd()
    } else {
        loadMoreCompleted()
    }
}

fun <T> BaseViewAdapter<T>.submitData(
    pageData: PagingData2<T>,
    diff: Boolean = false,
    refreshBlock: () -> Unit = {},
    emptyBlock: () -> Unit = {}
) {
    //首页加载逻辑
    if (pageData.isFirstPage()) {
        openLoadMore()

        // 过滤首页重复数据
        if (data == pageData.getDataSource()) {
            // 重复数据空数据校验
            if (pageData.getDataSource().isNullOrEmpty()) {
                if (isPageCompleted) initStatusView()
                clearData()
                pageEmpty()
                emptyBlock()
                return
            }
            // 刷新后首页数据即是末尾页
            if (pageData.isLastPage()) {
                loadMoreEnd()
                return
            }
            // 过滤重复数据
            return
        }

        //设置空页面
        if (pageData.getDataSource().isNullOrEmpty()) {
            if (isPageCompleted) initStatusView()
            clearData()
            pageEmpty()
            emptyBlock()
            return
        }

        //数据源不同替换数据更改状态
        if (data != pageData.getDataSource()) {
            //正常情况第一页加载数据状态
            updateData(pageData.getDataSource())
            pageCompleted()
            refreshBlock()
        }

        //分页加载逻辑
    } else {

        // 数据相同不处理
        if (data == pageData.getDataSource()) {
            return
        }

        //防止view重构后在分页加载时 pageCompleted状态重置
        if (!isPageCompleted) {
            pageCompleted()
        }

        if (diff) {
            //防止activity重建在viewModel中填充历史数据 做差分填充
            val oldData = data
            updateData(pageData.getDataSource())
            val result =
                DiffUtil.calculateDiff(DiffCallback<T>(oldData, pageData.getDataSource()), true)
            result.dispatchUpdatesTo(this)
        } else {
            addData(pageData.getDataSource())
        }
    }

    //设置尾页状态 (包括notifyDateSetChange)
    if (pageData.isLastPage()) {
        loadMoreEnd()
    } else {
        loadMoreCompleted()
    }
}

fun <T> BaseViewAdapter<T>.submitSinglePage(newData: List<T>) {
    if (data.isNullOrEmpty() && newData.isNullOrEmpty()) {
        pageEmpty()
    } else if (newData.isNotEmpty()) {
        updateData(newData)
        pageCompleted()
        loadMoreEnd()
    }
}

fun <T> BaseViewAdapter<T>.submitFailed() {
    if (isPageCompleted && !isItemEnd && data.isNotEmpty()) {
        loadFailed()
    } else if (!isPageCompleted && data.isEmpty()) {
        pageError()
    }
}
