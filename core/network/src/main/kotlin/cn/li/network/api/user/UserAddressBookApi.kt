package cn.li.network.api.user

import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.AddressBookAddDTO
import cn.li.network.dto.user.AddressBookDTO
import cn.li.network.dto.user.AddressBookUpdateDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 地址簿相关接口
 * */
internal interface UserAddressBookApi {

    /**
     * 新增地址
     * @param dto [AddressBookAddDTO]
     *
     * see: [文档](http://8.134.200.196:8080/doc.html#/user/%E5%9C%B0%E5%9D%80%E7%B0%BF%E6%8E%A5%E5%8F%A3/save)
     * */
    @POST("user/addressBook")
    suspend fun addNewAddress(@Body dto: AddressBookAddDTO): ApiResult<Nothing>

    /**
     * 更新地址
     * @param dto [AddressBookUpdateDTO]
     *
     * see: [文档](http://8.134.200.196:8080/doc.html#/user/%E5%9C%B0%E5%9D%80%E7%B0%BF%E6%8E%A5%E5%8F%A3/update)
     * */
    @PUT("user/addressBook")
    suspend fun updateAddress(@Body dto: AddressBookUpdateDTO): ApiResult<Nothing>

    /**
     * 根据id查地址
     * @param id 地址簿id
     *
     * see: [文档](http://8.134.200.196:8080/doc.html#/user/%E5%9C%B0%E5%9D%80%E7%B0%BF%E6%8E%A5%E5%8F%A3/getById)
     */
    @GET("user/addressBook/{id}")
    suspend fun getAddressBookById(@Path("id") id: Long): ApiResult<AddressBookAddDTO>

    /**
     * 根据id批量删除地址
     * @param ids 要删除的地址id
     *
     * see: [文档](http://8.134.200.196:8080/doc.html#/user/%E5%9C%B0%E5%9D%80%E7%B0%BF%E6%8E%A5%E5%8F%A3/delete)
     * */
    @DELETE("user/addressBook/{ids}")
    suspend fun deleteAddressBooksByIds(@Path("ids") ids: List<Long>): ApiResult<Nothing>

    /**
     * 查询默认地址
     *
     * see: [文旦](http://8.134.200.196:8080/doc.html#/user/%E5%9C%B0%E5%9D%80%E7%B0%BF%E6%8E%A5%E5%8F%A3/geeDefault)
     */
    @GET("user/addressBook/default")
    suspend fun getDefaultAddressBook(): ApiResult<AddressBookDTO>

    /**
     * 设置默认地址
     * @param id 设置默认地址的id
     *
     * see: [文档](http://8.134.200.196:8080/doc.html#/user/%E5%9C%B0%E5%9D%80%E7%B0%BF%E6%8E%A5%E5%8F%A3/setDefault)
     * */
    @PUT("user/addressBook/default")
    suspend fun setDefaultAddressBook(@Query("id") id: Long): ApiResult<Nothing>

    /**
     * 查询用户地址簿
     *
     * see: [文档](http://8.134.200.196:8080/doc.html#/user/%E5%9C%B0%E5%9D%80%E7%B0%BF%E6%8E%A5%E5%8F%A3/list)
     * */
    @GET("user/addressBook/list")
    suspend fun getUserAddressBooksList(): ApiResult<List<AddressBookDTO>>
}