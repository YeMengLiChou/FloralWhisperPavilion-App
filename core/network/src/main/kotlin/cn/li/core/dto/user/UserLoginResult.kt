package cn.li.core.dto.user

/**
 * 用户登录的返回结果
 * */
data class UserLoginResult(
    val id: Int,
    val token: String
)
