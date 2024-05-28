package cn.li.network.retrofit.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.li.network.api.user.UserOrderApi
import cn.li.network.api.user.UserOrderDataSource
import cn.li.network.dto.ApiPagination
import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.OrderDetailDTO
import cn.li.network.dto.user.OrderSubmitDTO
import cn.li.network.dto.user.OrderSubmitResultDTO
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

class RetrofitUserOrderDataSource @Inject constructor(
    retrofit: Retrofit
) : UserOrderDataSource {

    private val api = retrofit.create(UserOrderApi::class.java)


    override suspend fun getOrderDetail(id: Long): ApiResult<OrderDetailDTO> {
        return api.getOrderDetail(id)
    }

    override suspend fun cancelOrder(id: Long): ApiResult<Nothing> {
        return api.cancelOrder(id)
    }

    override suspend fun getHistoryOrders(queryMap: Map<String, Any>): ApiResult<ApiPagination<OrderDetailDTO>> {
        return api.getHistoryOrders(queryMap)
    }

    override suspend fun payOrder(): ApiResult<Nothing> {
        return api.payOrder()
    }

    override suspend fun submitOrder(dto: OrderSubmitDTO): ApiResult<OrderSubmitResultDTO> {
        return api.submitOrder(dto)
    }
}


@Singleton
class UserOrderPagingSource @Inject constructor(
    private val dataSource: UserOrderDataSource,
) : PagingSource<Int, OrderDetailDTO>() {


    private var completedOrder = false

    fun setCompletedOrder(completedOrder: Boolean) {
        this.completedOrder = completedOrder
    }

    override fun getRefreshKey(state: PagingState<Int, OrderDetailDTO>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OrderDetailDTO> {
        return try {
            val currentPage = params.key ?: 1
            val response = if (completedOrder) {
                dataSource.getHistoryOrders(mapOf())
            } else {
                dataSource.getHistoryOrders(mapOf())
            }

            val data = response.data

            LoadResult.Page(
                data = data!!.records,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (currentPage < response.data.pageNo) currentPage + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}