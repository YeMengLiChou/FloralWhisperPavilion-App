package cn.li.network.retrofit.datasource

import cn.li.network.api.user.UserApi
import cn.li.network.api.user.UserDataSource
import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.UserLoginAndRegisterDTO
import cn.li.network.dto.user.UserLoginResult
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