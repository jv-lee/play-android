package com.lee.playandroid.common.ui.widget

import com.lee.playandroid.base.adapter.listener.LoadResource
import com.lee.playandroid.common.R

/**
 * app通用列表加载状态layout资源
 * @author jv.lee
 * @date 2021/11/30
 */
class AppLoadResource : LoadResource {
    override fun pageLayoutId(): Int {
        return R.layout.layout_app_page_load
    }

    override fun pageLoadingId(): Int {
        return R.id.const_page_loading
    }

    override fun pageEmptyId(): Int {
        return R.id.const_page_empty
    }

    override fun pageErrorId(): Int {
        return R.id.const_page_error
    }

    override fun pageReloadId(): Int {
        return R.id.btn_restart
    }

    override fun itemLayoutId(): Int {
        return R.layout.layout_app_item_load
    }

    override fun itemLoadMoreId(): Int {
        return R.id.const_item_loadMore
    }

    override fun itemLoadEndId(): Int {
        return R.id.const_item_loadEnd
    }

    override fun itemLoadErrorId(): Int {
        return R.id.const_item_loadError
    }

    override fun itemReloadId(): Int {
        return R.id.tv_error_text
    }
}