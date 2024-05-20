package cn.li.network.dto.employee



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
    val token: String,
)
