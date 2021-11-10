package com.lee.playandroid.library.common.entity

import androidx.annotation.IntDef
import androidx.annotation.Keep
import androidx.room.*
import com.lee.library.db.converters.StringListConverter
import com.lee.playandroid.library.common.entity.ContentType.Companion.CONTENT
import com.lee.playandroid.library.common.entity.ContentType.Companion.PICTURE

/**
 * @author jv.lee
 * @date 2020/4/16
 * @description 数据库操作类
 */
@Target(
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER
)
@IntDef(CONTENT, PICTURE)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class ContentType {
    companion object {
        const val CONTENT = 0
        const val PICTURE = 1
    }
}

@Target(
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER
)
@IntDef(CONTENT, PICTURE)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class ContentSource {
    companion object {
        const val ID = 0
        const val URL = 1
    }
}

/**
 * 浏览记录
 * @param id 自增键
 * @param type 文字类型/妹子图类型
 * @param source id/url
 */
@Keep
@Entity
@TypeConverters(StringListConverter::class)
data class ContentHistory(
    @ColumnInfo(name = "history_id") @PrimaryKey(autoGenerate = false) var id: String,
    @ColumnInfo(name = "history_type") @ContentType val type: Int,
    @ColumnInfo(name = "history_source") @ContentSource val source: Int,
    @ColumnInfo(name = "read_time") val readTime: Long,
    @ColumnInfo(name = "is_collect") var isCollect: Int,
//    @Embedded val content: Content
) {
//    companion object {
//        fun parse(
//            @ContentType type: Int, @ContentSource source: Int, isCollect: Int, content: Content
//        ) =
//            ContentHistory(
//                content._id,
//                type,
//                source,
//                System.currentTimeMillis(),
//                isCollect,
//                content
//            )
//    }
}