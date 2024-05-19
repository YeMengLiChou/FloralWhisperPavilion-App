package cn.li.data.repository

import cn.li.datastore.FwpPreferencesDataStore
import cn.li.model.UserDataPreferences
import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.UserLoginAndRegisterDTO
import cn.li.network.dto.user.UserLoginResult
import cn.li.network.retrofit.datasource.RetrofitUserDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

internal class ApiUserRepository @Inject constructor(
    private val dataSource: RetrofitUserDataSource,
    private val dataStore: FwpPreferencesDataStore
) : UserRepository {

    override suspend fun userLogin(username: String, password: String): ApiResult<UserLoginResult> {
        return dataSource.userLogin(UserLoginAndRegisterDTO(username, password))
    }

    override suspend fun userRegister(username: String, password: String): ApiResult<String> {
        return dataSource.userRegister(UserLoginAndRegisterDTO(username, password))
    }

    override suspend fun setUserData(userDataPreferences: UserDataPreferences) {
        dataStore.updateUserData(
            userId = userDataPreferences.userId,
            token = userDataPreferences.token,
            lastLoginTime = userDataPreferences.lastLoginTimestamp
        )
    }



}