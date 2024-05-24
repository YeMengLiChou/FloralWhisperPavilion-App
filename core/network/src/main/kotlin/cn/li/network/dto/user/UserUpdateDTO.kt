package cn.li.network.dto.user

import cn.li.datastore.proto.UserPreferences


data class UserUpdateDTO(
    val id: Long,
    val nickname: String,
    val phone: String,
    val sex: Int,
    val avatar: String,
)

/**
 * 从 [UserPreferences] 中生成所需要的内容
 * */
fun UserPreferences.update(
    nickname: String = this.username, // TODO: 更改该字段
    phone: String = this.phone,
    sex: Int = this.sex,
    avatar: String = this.avatar
): UserUpdateDTO {
    return UserUpdateDTO(
        id = this.userId,
        nickname = nickname,
        phone = phone,
        sex = sex,
        avatar = avatar
    )
}


data class UserUpdatePasswordDTO(
    val oldPassword: String,
    val newPassword: String
)