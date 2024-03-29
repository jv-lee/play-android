/*
 * 数据库操作类
 * @author jv.lee
 * @date 2020/4/16
 */
package com.lee.playandroid.common.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 搜索历史数据库实体
 */
@Keep
@Entity
data class SearchHistory(
    @ColumnInfo(name = "search_history_key") @PrimaryKey(autoGenerate = false)
    val key: String,
    @ColumnInfo(name = "search_history_time") val time: Long = System.currentTimeMillis()
)
