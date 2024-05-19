package cn.li.data.repository

import cn.li.model.UserDataPreferences
import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.UserLoginAndRegisterDTO
import cn.li.network.dto.user.UserLoginResult
import kotlinx.coroutines.flow.Flow

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
    suspend fun setUserData(userDataPreferences: UserDataPreferences)
}