package cn.li.common.ext

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


private val zhFormatterHour = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")

private val zhFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日")

/**
 * 显示 xxxx年xx月xx日 xx:xx
 * */
fun Long.toZhDateString(showHour: Boolean = true): String {
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).format(
        if (showHour)
            zhFormatterHour
        else
            zhFormatter
    )
}


private val formatterHour = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")


fun Long.toDateString(showHour: Boolean = true): String {
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).format(
        if (showHour)
            formatterHour
        else
            formatter
    )
}