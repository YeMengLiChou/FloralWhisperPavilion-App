package cn.li.network.api.employee

import cn.li.network.dto.ApiResult
import cn.li.network.dto.employee.EmployeeLoginRegisterDTO
import cn.li.network.dto.employee.EmployeeLoginResult
import retrofit2.http.Body
import retrofit2.http.POST

interface EmployeeUserApi {

    /**
     * 员工登录
     * */
    @POST("employee/login")
    suspend fun employeeLogin(@Body dto: EmployeeLoginRegisterDTO): ApiResult<EmployeeLoginResult>


    /**
     * 员工注册
     * */
    @POST("employee/register")
    suspend fun employeeRegister(@Body dto: EmployeeLoginRegisterDTO): ApiResult<String>
}

interface EmployeeDataSource {
    suspend fun employeeLogin( dto: EmployeeLoginRegisterDTO): ApiResult<EmployeeLoginResult>

    suspend fun employeeRegister( dto: EmployeeLoginRegisterDTO): ApiResult<String>

}