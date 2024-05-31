package cn.li.network.api.user

import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.CommodityDetailDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface UserCommodityApi {

    @GET("user/dish/getDishById")
    suspend fun getDetailById(@Query("id") commodityId: Long): ApiResult<CommodityDetailDTO>

}


interface UserCommodityDataSource {

    suspend fun getDetailById(commodityId: Long): ApiResult<CommodityDetailDTO>
}