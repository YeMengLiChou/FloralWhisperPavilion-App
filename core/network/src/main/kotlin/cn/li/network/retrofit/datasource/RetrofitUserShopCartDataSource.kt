package cn.li.network.retrofit.datasource

import cn.li.network.api.user.UserShoppingCartApi
import cn.li.network.api.user.UserShoppingCartDataSource
import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.ShoppingCartAddDTO
import cn.li.network.dto.user.ShoppingCartDTO
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitUserShopCartDataSource @Inject constructor(
    retrofit: Retrofit
): UserShoppingCartDataSource {
    
    
    private val api = retrofit.create(UserShoppingCartApi::class.java)

    override suspend fun addCommodityToShoppingCart(dto: ShoppingCartAddDTO): ApiResult<Nothing> {
        return api.addCommodityToShoppingCart(dto)
    }

    override suspend fun clearShoppingCart(shopId: Long): ApiResult<Nothing> {
        return api.clearShoppingCart(shopId)
    }

    override suspend fun getShoppingCartList(shopId: Long): ApiResult<List<ShoppingCartDTO>> {
        return api.getShoppingCartList(shopId)
    }

    override suspend fun updateShoppingCartCommodityCount(
        ids: String,
        counts: String
    ): ApiResult<Nothing> {
        return api.updateShoppingCartCommodityCount(ids, counts)
    }
}