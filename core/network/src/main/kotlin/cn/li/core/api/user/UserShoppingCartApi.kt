package cn.li.core.api.user

import cn.li.core.dto.ApiResult
import cn.li.core.dto.user.ShoppingCartAddDTO
import cn.li.core.dto.user.ShoppingCartDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 购物车相关接口
 * */
internal interface UserShoppingCartApi {
    /**
     * 添加商品到购物车
     * @param dto [ShoppingCartDTO]
     *
     * see: [文档](http://8.134.200.196:8080/doc.html#/user/%E8%B4%AD%E7%89%A9%E8%BD%A6%E7%9B%B8%E5%85%B3%E6%8E%A5%E5%8F%A3/add)
     */
    @POST("user/shoppingCar/add")
    suspend fun addDishToShoppingCart(@Body dto: ShoppingCartAddDTO): ApiResult<Nothing>

    /**
     * 清空购物车
     * @param shopId 所在商店的id
     *
     * see: [文档](http://8.134.200.196:8080/doc.html#/user/%E8%B4%AD%E7%89%A9%E8%BD%A6%E7%9B%B8%E5%85%B3%E6%8E%A5%E5%8F%A3/clean)
     */
    @DELETE("user/shoppingCar/clean")
    suspend fun clearShoppingCart(@Query("shopId") shopId: Long): ApiResult<Nothing>

    /**
     * 查询购物车
     * @param shopId 所在商店id
     *
     * see: [文档](http://8.134.200.196:8080/doc.html#/user/%E8%B4%AD%E7%89%A9%E8%BD%A6%E7%9B%B8%E5%85%B3%E6%8E%A5%E5%8F%A3/list_1)
     * */
    @GET("user/shoppingCar/list")
    suspend fun getShoppingCartList(@Query("shopId") shopId: Long): ApiResult<ShoppingCartDTO>

    /**
     * 修改购物车商品信息
     *
     *
     * */
    @POST("user/shoppingCar/updateNumber")
    suspend fun updateShoppingCartDishCount(
        @Query("shoppingCartId") shoppingCartId: Long,
        @Query("count") count: Int
    ): ApiResult<Nothing>
}