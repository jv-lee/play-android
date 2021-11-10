package com.lee.playandroid.library.service

import com.lee.library.adapter.item.ViewBindingItem
import com.lee.playandroid.library.service.core.IModuleService

/**
 * @author jv.lee
 * @data 2021/9/9
 * @description
 */
interface HomeService : IModuleService {
    fun getContentMultipleItem(): ViewBindingItem<*>
    fun getContentSingleItem(): ViewBindingItem<*>
    fun getContentTextItem(): ViewBindingItem<*>

}