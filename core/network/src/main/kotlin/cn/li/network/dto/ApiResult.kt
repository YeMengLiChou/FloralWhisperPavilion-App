package cn.li.network.dto

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * 自定义 api 结果类
 * */
data class ApiResult<out T>(
    val code: Int,
    val msg: String,
    val data: T?
) {

}

fun <T> ApiResult<T>.onSuccess(
    vararg codes: Int = intArrayOf(200),
    dataNullable: Boolean = true,
    action: (T?) -> Unit
): ApiResult<T>? {
    for (code in codes) {
        if (this.code == code) {
            if (dataNullable) {
                action.invoke(data)
                return null
            }
            if (!dataNullable && data != null) {
                action.invoke(data)
                return null
            }
        }
    }
    return this
}

fun <T> ApiResult<T>.onError(action: (msg: String) -> Unit) {
    action(this.msg)
}