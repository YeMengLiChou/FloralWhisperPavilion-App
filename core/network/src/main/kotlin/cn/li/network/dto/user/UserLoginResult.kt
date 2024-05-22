package cn.li.network.dto.user

import cn.li.datastore.UserPreferences
import cn.li.datastore.copy
import cn.li.model.constant.AppRole
import cn.li.model.ext.toTimestamp

/**
 * 用户登录的返回结果
 * */
data class UserLoginResult(
    val id: Long,
    val username: String,
    val avatar: String,
    val createTime: String,
    val updateTime: String,
    val phone: String,
    val sex: String,
)


fun UserPreferences.update(data: UserLoginResult): UserPreferences {
    return copy {
        userId = data.id
        username = data.username
        avatar = data.avatar
        createTime = data.createTime.toTimestamp()
        updateTime = data.updateTime.toTimestamp()
        phone = data.phone
        sex = data.sex.toInt()
        identification = AppRole.USER
        loginTimestamp = System.currentTimeMillis()
        shopId = 0
    }
}
