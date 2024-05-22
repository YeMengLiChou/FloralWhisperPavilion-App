package cn.li.network.dto.employee

import cn.li.datastore.UserPreferences
import cn.li.datastore.copy
import cn.li.model.constant.AppRole
import cn.li.model.ext.toTimestamp
import cn.li.network.dto.user.UserLoginResult


/**
 * 员工
 * */
data class EmployeeLoginRegisterDTO(
    val username: String,
    val password: String
)


/**
 * 员工登录的响应结果
 * */
data class EmployeeLoginResult(
    val id: Long,
    val shopId: Long,
    val username: String,
    val avatar: String,
    val createTime: String,
    val updateTime: String,
    val phone: String,
    val sex: String,
)

fun UserPreferences.update(data: EmployeeLoginResult): UserPreferences {
    return copy {
        userId = data.id
        username = data.username
        avatar = data.avatar
        createTime = data.createTime.toTimestamp()
        updateTime = data.updateTime.toTimestamp()
        phone = data.phone
        sex = data.sex.toInt()
        identification = AppRole.EMPLOYEE
        loginTimestamp = System.currentTimeMillis()
        shopId = data.shopId
    }
}

