package cn.li.data.repository

import cn.li.datastore.FwpPreferencesDataStore
import cn.li.datastore.proto.UserPreferences
import cn.li.network.api.employee.EmployeeDataSource
import cn.li.network.api.user.UserDataSource
import cn.li.network.dto.ApiResult
import cn.li.network.dto.employee.EmployeeLoginRegisterDTO
import cn.li.network.dto.employee.EmployeeLoginResult
import cn.li.network.dto.user.UserLoginAndRegisterDTO
import cn.li.network.dto.user.UserLoginResult
import cn.li.network.dto.user.UserUpdateDTO
import cn.li.network.retrofit.datasource.RetrofitEmployeeDataSource
import cn.li.network.retrofit.datasource.RetrofitUserDataSource
import javax.inject.Inject

/**
 * 集成 Api 网络的数据仓库
 * */
internal class ApiUserRepository @Inject constructor(
    private val userDataSource: UserDataSource,
    private val employeeDataSource: EmployeeDataSource,
    private val dataStore: FwpPreferencesDataStore
) : UserRepository {

    /**
     * 用户登录
     * */
    override suspend fun userLogin(username: String, password: String): ApiResult<UserLoginResult> {
        return userDataSource.userLogin(UserLoginAndRegisterDTO(username, password))
    }

    /**
     * 用户注册
     * */
    override suspend fun userRegister(username: String, password: String): ApiResult<String> {
        return userDataSource.userRegister(UserLoginAndRegisterDTO(username, password))
    }

    /**
     * 员工登录
     * */
    override suspend fun employeeLogin(
        username: String,
        password: String
    ): ApiResult<EmployeeLoginResult> {
        return employeeDataSource.employeeLogin(EmployeeLoginRegisterDTO(username, password))
    }

    override suspend fun updateUserInfo(userUpdateDTO: UserUpdateDTO): ApiResult<String?> {
        return userDataSource.updateUserInfo(userUpdateDTO)
    }

    override suspend fun getUserInfo(id: Long): ApiResult<UserLoginResult> {
        return userDataSource.getUserInfo(id)
    }

    override suspend fun updateUserPassword(old: String, new: String): ApiResult<String> {
        return userDataSource.updateUserPassword(old, new)
    }

    /**
     * 更新用户数据缓存
     * */
    override suspend fun setUserData(userPreferences: UserPreferences) {
        dataStore.updateUserData { userPreferences }
    }


}