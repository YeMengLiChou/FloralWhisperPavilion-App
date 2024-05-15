package cn.li.core.retrofit.datasource

import cn.li.core.api.user.UserApi
import cn.li.core.api.user.UserDataSource
import cn.li.core.dto.ApiResult
import cn.li.core.dto.user.UserLoginAndRegisterDTO
import cn.li.core.dto.user.UserLoginResult
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
}