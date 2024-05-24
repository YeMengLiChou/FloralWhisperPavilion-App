package cn.li.network.api.user

import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.UserLoginAndRegisterDTO
import cn.li.network.dto.user.UserLoginResult
import cn.li.network.dto.user.UserUpdateDTO
import cn.li.network.dto.user.UserUpdatePasswordDTO
import com.google.protobuf.Api
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query


internal interface UserApi {

    /**
     * 用户登录接口
     *
     * **See:** [文档](http://8.134.200.196:8080/doc.html#/user/%E7%94%A8%E6%88%B7%E6%A8%A1%E5%9D%97/login)
     * @param dto 用户名 [UserLoginAndRegisterDTO.username] 和 密码 [UserLoginAndRegisterDTO.password]
     * @return [ApiResult]<[UserLoginResult]>
     * */
    @POST("user/user/login")
    suspend fun userLogin(@Body dto: UserLoginAndRegisterDTO): ApiResult<UserLoginResult>

    /**
     * 用户注册接口
     *
     * **See:** [文档](http://8.134.200.196:8080/doc.html#/user/%E7%94%A8%E6%88%B7%E6%A8%A1%E5%9D%97/login)
     * @param dto 用户名 [UserLoginAndRegisterDTO.username] 和 密码 [UserLoginAndRegisterDTO.password]
     * @return [ApiResult]<String>
     * */
    @POST("user/user/register")
    suspend fun userRegister(@Body dto: UserLoginAndRegisterDTO): ApiResult<String>


    @PUT("user/user/update")
    suspend fun updateUserInfo(@Body dto: UserUpdateDTO): ApiResult<String?>


    @POST("user/user/updatePassword")
    suspend fun updatePassword(@Body dto: UserUpdatePasswordDTO): ApiResult<Nothing>

    @GET("user/user/getUserById")
    suspend fun getUserInfo(@Query("id") id: Long): ApiResult<UserLoginResult>
}

interface UserDataSource {

    suspend fun userLogin(dto: UserLoginAndRegisterDTO): ApiResult<UserLoginResult>

    suspend fun userRegister(dto: UserLoginAndRegisterDTO): ApiResult<String>

    suspend fun updateUserInfo(dto: UserUpdateDTO): ApiResult<String?>

    suspend fun updateUserPassword(old: String, new: String): ApiResult<String>

    suspend fun getUserInfo(id: Long): ApiResult<UserLoginResult>

}