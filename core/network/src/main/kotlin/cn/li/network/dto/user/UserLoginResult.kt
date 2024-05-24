package cn.li.network.dto.user


import cn.li.datastore.proto.UserPreferences
import cn.li.datastore.proto.copy
import cn.li.model.constant.AppRole
import cn.li.model.ext.toTimestamp

/**
 * 用户登录的返回结果
 * */
data class UserLoginResult(
    val id: Long,
    val username: String,
    val avatar: String?,
    val createTime: String,
    val updateTime: String,
    val phone: String?,
    val sex: String?,
    val nickname: String,
) {
    companion object {
        /**
         * 未指定的性别
         * */
        const val SEX_UNSPECIFIED = -1
        const val SEX_MALE = 1
        const val SEX_FEMALE = 0
    }
}


fun UserPreferences.update(data: UserLoginResult): UserPreferences {
    return copy {
        userId = data.id
        username = data.username
        avatar = data.avatar ?: ""
        createTime = data.createTime.toTimestamp()
        updateTime = data.updateTime.toTimestamp()
        phone = data.phone ?: ""
        sex = data.sex?.toInt() ?: UserLoginResult.SEX_UNSPECIFIED
        identification = AppRole.USER
        loginTimestamp = System.currentTimeMillis()
        shopId = 0
        nickname = data.nickname
    }
}
