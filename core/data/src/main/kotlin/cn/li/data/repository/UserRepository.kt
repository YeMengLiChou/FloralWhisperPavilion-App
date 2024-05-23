package cn.li.data.repository

import cn.li.datastore.UserPreferences
import cn.li.network.dto.ApiResult
import cn.li.network.dto.employee.EmployeeLoginResult
import cn.li.network.dto.user.UserLoginResult

/**
 * 用户相关的数据源
 * */
interface UserRepository {

    /**
     * 用户登录
     * */
    suspend fun userLogin(username: String, password: String): ApiResult<UserLoginResult>

    /**
     * 用户注册
     * */
    suspend fun userRegister(username: String, password: String): ApiResult<String>


    /**
     * 存储用户数据
     * */
    suspend fun setUserData(userPreferences: UserPreferences)

    /**
     * 员工能录
     * */
    suspend fun employeeLogin(username: String, password: String): ApiResult<EmployeeLoginResult>
}