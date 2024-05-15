package cn.li.core.retrofit

data class ApiResult<T>(
    val code: Int,
    val msg: String,
    val data: T
)
