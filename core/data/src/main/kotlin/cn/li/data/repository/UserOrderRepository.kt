package cn.li.data.repository

import androidx.paging.PagingData
import cn.li.network.dto.ApiPagination
import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.OrderDetailDTO
import cn.li.network.dto.user.OrderSubmitDTO
import cn.li.network.dto.user.OrderSubmitResultDTO
import kotlinx.coroutines.flow.Flow

interface UserOrderRepository {

    suspend fun getOrderDetail(id: Long): ApiResult<OrderDetailDTO>

    suspend fun cancelOrder(id: Long): ApiResult<Nothing>

    suspend fun getHistoryOrders(queryMap: Map<String, Any>): ApiResult<ApiPagination<OrderDetailDTO>>

    suspend fun getUncompletedOrders(
        pageNo: Int, pageSize: Int
    ): ApiResult<ApiPagination<OrderDetailDTO>>

    fun getUncompletedOrderPagingData(
        pageSize: Int = 20,
        prefetchDistance: Int = 10
    ): Flow<PagingData<OrderDetailDTO>>

    suspend fun getCompletedOrders(
        pageNo: Int, pageSize: Int
    ): ApiResult<ApiPagination<OrderDetailDTO>>

    suspend fun payOrder(): ApiResult<Nothing>

    suspend fun submitOrder(dto: OrderSubmitDTO): ApiResult<OrderSubmitResultDTO>

    fun getCompletedOrderPagingData(
        pageSize: Int = 20,
        prefetchDistance: Int = 10
    ): Flow<PagingData<OrderDetailDTO>>
}