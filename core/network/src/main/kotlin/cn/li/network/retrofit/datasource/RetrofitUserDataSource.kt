package cn.li.network.retrofit.datasource

import cn.li.network.api.user.UserApi
import cn.li.network.api.user.UserDataSource
import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.UserLoginAndRegisterDTO
import cn.li.network.dto.user.UserLoginResult
import cn.li.network.dto.user.UserUpdateDTO
import cn.li.network.dto.user.UserUpdatePasswordDTO
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户登录的网络数据源
 * */
@Singleton
class RetrofitUserDataSource @Inject constructor(
    retrofit: Retrofit
): UserDataSource {

    private val userApi = retrofit.create(UserApi::class.java)

    override suspend fun userLogin(dto: UserLoginAndRegisterDTO): ApiResult<UserLoginResult> {
        return userApi.userLogin(dto)
    }

    override suspend fun userRegister(dto: UserLoginAndRegisterDTO): ApiResult<String> {
        return userApi.userRegister(dto)
    }

    override suspend fun getUserInfo(id: Long): ApiResult<UserLoginResult> {
        return userApi.getUserInfo(id)
    }

    override suspend fun updateUserInfo(dto: UserUpdateDTO): ApiResult<String?> {
        return userApi.updateUserInfo(dto)
    }

    override suspend fun updateUserPassword(old: String, new: String): ApiResult<String> {
        return userApi.updatePassword(UserUpdatePasswordDTO(
            newPassword = new,
            oldPassword = old
        ))
    }
}