package com.lee.playandroid.library.common.extensions

import androidx.core.text.HtmlCompat
import com.lee.library.utils.TimeUtil
import com.lee.playandroid.library.common.entity.Content

/**
 *
 * @author jv.lee
 * @date 2022/3/2
 */

fun Content.getTitle(): String =
    HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

fun Content.getAuthor(): String = author.ifEmpty { shareUser }

fun Content.getDateFormat(): String = TimeUtil.getChineseTimeMill(publishTime)

fun Content.getCategory(): String {
    return when {
        superChapterName.isNotEmpty() and chapterName.isNotEmpty() -> "$superChapterName / $chapterName"
        superChapterName.isNotEmpty() -> superChapterName
        chapterName.isNotEmpty() -> chapterName
        else -> ""
    }
}