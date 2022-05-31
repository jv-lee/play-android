package com.lee.playandroid.todo.extensions

import com.lee.library.utils.TimeUtil
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @author jv.lee
 * @date 2021/12/30
 */
fun stringToCalendar(dateStr: String): Calendar {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = sdf.parse(dateStr)!!
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar
}

fun dateToStrFormat(): String {
    return TimeUtil.date2String(Date(), SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()))
}