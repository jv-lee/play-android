package com.lee.playandroid.me.ui.widget

import com.lee.playandroid.base.adapter.listener.LoadResource
import com.lee.playandroid.me.R
import com.lee.playandroid.base.R as BR

/**
 * 积分列表加载item状态resource
 * @author jv.lee
 * @date 2021/11/30
 */
class CoinLoadResource : LoadResource {
    override fun pageLayoutId(): Int {
        return R.layout.layout_coin_page_load
    }

    override fun pageLoadingId(): Int {
        return R.id.const_page_loading
    }

    override fun pageEmptyId(): Int {
        return R.id.const_page_empty
    }

    override fun pageEmptyTextId(): Int {
        return R.id.tv_empty_text
    }

    override fun pageErrorId(): Int {
        return R.id.const_page_error
    }

    override fun pageNetworkId(): Int {
        return R.id.const_page_error
    }

    override fun pageReloadId(): Int {
        return R.id.btn_restart
    }

    override fun itemLayoutId(): Int {
        return BR.layout.layout_item_load
    }

    override fun itemLoadMoreId(): Int {
        return BR.id.const_item_loadMore
    }

    override fun itemLoadEndId(): Int {
        return BR.id.const_item_loadEnd
    }

    override fun itemLoadErrorId(): Int {
        return BR.id.const_item_loadError
    }

    override fun itemReloadId(): Int {
        return BR.id.tv_error_text
    }
}