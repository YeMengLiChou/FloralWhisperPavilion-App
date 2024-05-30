package cn.li.network.api.user

import cn.li.network.dto.ApiPagination
import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.OrderDetailDTO
import cn.li.network.dto.user.OrderSubmitDTO
import cn.li.network.dto.user.OrderSubmitResultDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * 用户订单相关接口
 * */
internal interface UserOrderApi {

    /**
     * 获取订单详情
     * @param id 用户 id
     *
     * See: [文档](http://8.134.200.196:8080/doc.html#/user/%E7%94%A8%E6%88%B7%E8%AE%A2%E5%8D%95%E7%9B%B8%E5%85%B3%E6%8E%A5%E5%8F%A3/details)
     * */
    @GET("user/order/orderDetail/{id}")
    suspend fun getOrderDetail(@Path("id") id: Long): ApiResult<OrderDetailDTO>

    /**
     * 取消订单
     * @param id 用户 id
     * @return
     * See: [文档](http://8.134.200.196:8080/doc.html#/user/%E7%94%A8%E6%88%B7%E8%AE%A2%E5%8D%95%E7%9B%B8%E5%85%B3%E6%8E%A5%E5%8F%A3/details)
     * */
    @PUT("user/order/cancel/{id}")
    suspend fun cancelOrder(@Path("id") id: Long): ApiResult<Nothing>

    /**
     * 获取历史订单
     * @param queryMap 请求参数
     * @see [OrderDetailDTO]
     * See: [文档](http://8.134.200.196:8080/doc.html#/user/%E7%94%A8%E6%88%B7%E8%AE%A2%E5%8D%95%E7%9B%B8%E5%85%B3%E6%8E%A5%E5%8F%A3/page)
     * */
    @GET("user/order/page")
    suspend fun getHistoryOrders(@QueryMap queryMap: Map<String, Any>): ApiResult<ApiPagination<OrderDetailDTO>>

    /**
     * 支付订单
     *
     * See: [文档](http://8.134.200.196:8080/doc.html#/user/%E7%94%A8%E6%88%B7%E8%AE%A2%E5%8D%95%E7%9B%B8%E5%85%B3%E6%8E%A5%E5%8F%A3/paySuceess)
     * */
    @PUT("user/oder/pay")
    suspend fun payOrder(): ApiResult<Nothing>

    /**
     * 下单
     * @param dto [OrderSubmitDTO]
     * @return [ApiResult]<[OrderSubmitResultDTO]>
     *
     * See: [文档](http://8.134.200.196:8080/doc.html#/user/%E7%94%A8%E6%88%B7%E8%AE%A2%E5%8D%95%E7%9B%B8%E5%85%B3%E6%8E%A5%E5%8F%A3/submit)
     * */
    @POST("user/order/submit")
    suspend fun submitOrder(@Body dto: OrderSubmitDTO): ApiResult<OrderSubmitResultDTO>

    @GET("user/order/is-finish")
    suspend fun fetchOrdersList(
        @Query("status") status: Int,
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int
    ): ApiResult<ApiPagination<OrderDetailDTO>>
}


interface UserOrderDataSource {
    suspend fun getOrderDetail(id: Long): ApiResult<OrderDetailDTO>

    suspend fun cancelOrder(id: Long): ApiResult<Nothing>

    suspend fun getHistoryOrders(queryMap: Map<String, Any>): ApiResult<ApiPagination<OrderDetailDTO>>

    suspend fun payOrder(): ApiResult<Nothing>

    suspend fun submitOrder(dto: OrderSubmitDTO): ApiResult<OrderSubmitResultDTO>

    suspend fun fetchOrdersList(
        status: Int,
        pageNo: Int,
        pageSize: Int
    ): ApiResult<ApiPagination<OrderDetailDTO>>
}