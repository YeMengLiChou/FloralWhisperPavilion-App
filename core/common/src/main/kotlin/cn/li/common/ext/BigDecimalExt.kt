package cn.li.common.ext

import java.math.BigDecimal
import java.math.RoundingMode

operator fun BigDecimal.plus(other: BigDecimal): BigDecimal {
    return this.add(other)
}

/**
 * 计算金额，保留两位小数
 *
 * */
fun <T> Iterable<T>.sumAmountOf(selector: (T) -> Double): BigDecimal {
    var sum = BigDecimal.ZERO
    for (element in this) {
        sum += BigDecimal.valueOf(selector(element))
    }
    return sum.setScale(2, RoundingMode.FLOOR)
}