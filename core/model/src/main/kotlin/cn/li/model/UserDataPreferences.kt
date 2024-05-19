package cn.li.model

/**
 * 用户数据
 * @param userId 用户id
 * @param token jwt
 * @param lastLoginTimestamp 上次登录时间
 * */
data class UserDataPreferences(
    val userId: String,
    val token: String,
    val lastLoginTimestamp: Long = System.currentTimeMillis()
)
