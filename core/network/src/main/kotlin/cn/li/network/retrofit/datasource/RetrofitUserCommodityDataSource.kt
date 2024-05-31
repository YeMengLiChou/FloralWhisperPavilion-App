package cn.li.network.retrofit.datasource

import cn.li.network.api.user.UserCommodityApi
import cn.li.network.api.user.UserCommodityDataSource
import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.CommodityDetailDTO
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitUserCommodityDataSource @Inject constructor(
    retrofit: Retrofit
): UserCommodityDataSource {
    private val api = retrofit.create(UserCommodityApi::class.java)

    override suspend fun getDetailById(commodityId: Long): ApiResult<CommodityDetailDTO> {
        return api.getDetailById(commodityId)
    }
}