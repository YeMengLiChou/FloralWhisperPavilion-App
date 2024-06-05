package cn.li.network.retrofit.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.li.network.api.user.UserOrderApi
import cn.li.network.api.user.UserOrderDataSource
import cn.li.network.dto.ApiPagination
import cn.li.network.dto.ApiPagination1
import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.OrderDetailDTO
import cn.li.network.dto.user.OrderRecordDTO
import cn.li.network.dto.user.OrderSubmitDTO
import cn.li.network.dto.user.OrderSubmitResultDTO
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

class RetrofitUserOrderDataSource @Inject constructor(
    retrofit: Retrofit
) : UserOrderDataSource {

    private val api = retrofit.create(UserOrderApi::class.java)


    override suspend fun getOrderDetail(id: Long): ApiResult<OrderRecordDTO> {
        return api.getOrderDetail(id)
    }

    override suspend fun cancelOrder(id: Long): ApiResult<Nothing> {
        return api.cancelOrder(id)
    }

    override suspend fun getHistoryOrders(queryMap: Map<String, Any>): ApiResult<ApiPagination1<OrderRecordDTO>> {
        return api.getHistoryOrders(queryMap)
    }

    override suspend fun payOrder(orderNumber: String): ApiResult<Nothing> {
        return api.payOrder(orderNumber)
    }

    override suspend fun submitOrder(dto: OrderSubmitDTO): ApiResult<OrderSubmitResultDTO> {
        return api.submitOrder(dto)
    }

    override suspend fun fetchOrdersList(
        status: Int,
        pageNo: Int,
        pageSize: Int
    ): ApiResult<ApiPagination1<OrderRecordDTO>> {
        return api.fetchOrdersList(status, pageNo, pageSize)
    }
}


class UserOrderPagingSource @Inject constructor(
    private val dataSource: UserOrderDataSource,
    private val completedOrder: Boolean
) : PagingSource<Int, OrderRecordDTO>() {

    override fun getRefreshKey(state: PagingState<Int, OrderRecordDTO>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OrderRecordDTO> {
        return try {
            val currentPage = params.key ?: 1

            val response = dataSource.fetchOrdersList(
                status = if (completedOrder) 1 else 0,
                pageNo = currentPage,
                pageSize = params.loadSize
            )

            Log.d("UserOrderViewModel", "load: $response")
            val data = response.data
            LoadResult.Page(
                data = data!!.records,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (currentPage < response.data.current) currentPage + 1 else null
            )
        } catch (e: Exception) {
            Log.e("UserOrderViewModel", "load-error: ", e)
            LoadResult.Error(e)
        }
    }
}