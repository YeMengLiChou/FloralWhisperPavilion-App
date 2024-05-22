package cn.li.model.ext

import java.sql.Date
import java.time.Instant

/**
 * 将数据库中的 Date String 转换为时间戳
 *
 * Ex: "2024-05-22T04:36:50.000+00:00".[toTimestamp] () -> 1716352610000
 * */
fun String.toTimestamp(): Long {
    val date = Date.from(Instant.parse(this))
    return date.time
}

