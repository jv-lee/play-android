package com.lee.pioneer.library.service

import com.lee.library.db.base.BaseDao
import com.lee.pioneer.library.common.entity.ContentHistory
import com.lee.pioneer.library.service.core.IModuleService

/**
 * @author jv.lee
 * @date 2021/9/9
 * @description
 */
interface MeService : IModuleService, BaseDao<ContentHistory> {
    /**
     * 查询所有浏览记录
     */
    fun queryContentHistory(limit: Int): List<ContentHistory>

    /**
     * 获取浏览记录总条数
     */
    fun queryContentHistoryCount(): Int

    /**
     * 查询所有收藏的内容
     */
    fun queryContentCollect(limit: Int): List<ContentHistory>

    /**
     * 获取收藏内容总条数
     */
    fun queryContentCollectCount(): Int

    /**
     * 查询该条记录是否点击收藏
     */
    fun isCollect(id: String): Int

    /**
     * 通过id 查询出一条内容
     */
    fun queryContentById(id: String): List<ContentHistory>

}