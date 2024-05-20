package cn.li.network.retrofit.datasource

import cn.li.network.api.employee.EmployeeUserApi
import cn.li.network.api.employee.EmployeeDataSource
import cn.li.network.dto.ApiResult
import cn.li.network.dto.employee.EmployeeLoginRegisterDTO
import cn.li.network.dto.employee.EmployeeLoginResult
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RetrofitEmployeeDataSource @Inject constructor(
    retrofit: Retrofit
): EmployeeDataSource {
    private val employeeApi = retrofit.create(EmployeeUserApi::class.java)

    override suspend fun employeeLogin(dto: EmployeeLoginRegisterDTO): ApiResult<EmployeeLoginResult> {
        return employeeApi.employeeLogin(dto)
    }

    override suspend fun employeeRegister(dto: EmployeeLoginRegisterDTO): ApiResult<String> {
        return employeeApi.employeeRegister(dto)
    }
}