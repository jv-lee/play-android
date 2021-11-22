package com.lee.playandroid.search.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.lee.library.db.base.BaseDao
import com.lee.playandroid.library.common.entity.SearchHistory

/**
 * @author jv.lee
 * @date 2021/11/22
 * @description
 */
@Dao
interface SearchHistoryDao : BaseDao<SearchHistory> {

    /**
     * 查询最近搜索倒序10条数据
     */
    @Query("SELECT * FROM SearchHistory ORDER BY search_history_time DESC LIMIT 0,10")
    fun querySearchHistory(): List<SearchHistory>

}