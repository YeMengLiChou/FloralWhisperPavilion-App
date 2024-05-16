package cn.li.core.dto

/**
 * 自定义 api 结果类
 * */
data class ApiResult<T>(
    val code: Int,
    val msg: String,
    val data: T?
)

