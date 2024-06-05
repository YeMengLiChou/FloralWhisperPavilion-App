package cn.li.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import cn.li.network.api.user.UserOrderDataSource
import cn.li.network.dto.ApiPagination
import cn.li.network.dto.ApiPagination1
import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.OrderDetailDTO
import cn.li.network.dto.user.OrderRecordDTO
import cn.li.network.dto.user.OrderSubmitDTO
import cn.li.network.dto.user.OrderSubmitResultDTO
import cn.li.network.retrofit.datasource.UserOrderPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiUserOrderRepository
@Inject constructor(
    private val dataSource: UserOrderDataSource,
) : UserOrderRepository {



    override suspend fun getOrderDetail(id: Long): ApiResult<OrderRecordDTO> {
        return dataSource.getOrderDetail(id)
    }

    override suspend fun cancelOrder(id: Long): ApiResult<Nothing> {
        return dataSource.cancelOrder(id)
    }

    override suspend fun getHistoryOrders(queryMap: Map<String, Any>): ApiResult<ApiPagination1<OrderRecordDTO>> {
        return dataSource.getHistoryOrders(queryMap)
    }

    override suspend fun getUncompletedOrders(
        pageNo: Int,
        pageSize: Int
    ): ApiResult<ApiPagination1<OrderRecordDTO>> {
        return getHistoryOrders(
            mapOf(
                "pageNo" to pageNo,
                "pageSize" to pageSize,
                "status" to 1,
                "sortBy" to "order_time",
                "isAsc" to true,
            )
        )
    }


    override fun getUncompletedOrderPagingData(
        pageSize: Int,
        prefetchDistance: Int
    ): Flow<PagingData<OrderRecordDTO>> {
        return Pager(
            PagingConfig(
                pageSize = pageSize,
                prefetchDistance = prefetchDistance,
                enablePlaceholders = false
            )
        ) {
            UserOrderPagingSource(
                dataSource = dataSource,
                completedOrder = false
            )
        }.flow
    }

    override fun getCompletedOrderPagingData(
        pageSize: Int,
        prefetchDistance: Int
    ): Flow<PagingData<OrderRecordDTO>> {
        return Pager(
            PagingConfig(
                pageSize = pageSize,
                prefetchDistance = prefetchDistance,
                enablePlaceholders = false
            )
        ) {
            UserOrderPagingSource(
                dataSource = dataSource,
                completedOrder = true
            )
        }.flow
    }

    override suspend fun getCompletedOrders(
        pageNo: Int,
        pageSize: Int
    ): ApiResult<ApiPagination1<OrderRecordDTO>> {
        return getHistoryOrders(
            mapOf(
                "pageNo" to pageNo,
                "pageSize" to pageSize,
                "status" to 1,
                "sortBy" to "order_time",
                "isAsc" to true,
            )
        )
    }

    override suspend fun payOrder(orderNumber: String): ApiResult<Nothing> {
        return dataSource.payOrder(orderNumber)
    }

    override suspend fun submitOrder(dto: OrderSubmitDTO): ApiResult<OrderSubmitResultDTO> {
        return dataSource.submitOrder(dto)
    }
}