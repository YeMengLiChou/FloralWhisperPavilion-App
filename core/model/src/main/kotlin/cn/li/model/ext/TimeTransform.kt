package cn.li.model.ext

import android.util.Log
import java.sql.Date
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

/**
 * 将数据库中的 Date String 转换为时间戳
 *
 * Ex: "2024-05-22T04:36:50.000+00:00".[toTimestamp] () -> 1716352610000
 * */
fun String.toTimestamp(): Long {
    val zoneDateTIme = ZonedDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    return zoneDateTIme.toInstant().toEpochMilli()
}

/**
 * 将 Long 型的时间戳转换为 "2024-05-22T04:36:50.000+00:00" 格式的字符串
 * */
fun Long.timeFormat(): String {
    val instant = Instant.ofEpochMilli(this)
    // 将 Instant 转换为 ZonedDateTime，使用 UTC 时区
    val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)

    // 格式化为指定的字符串格式
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    return zonedDateTime.format(formatter)
}
