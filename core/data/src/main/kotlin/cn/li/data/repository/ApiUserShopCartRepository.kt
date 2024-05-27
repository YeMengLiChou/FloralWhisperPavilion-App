package cn.li.data.repository

import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.ShoppingCartAddDTO
import cn.li.network.dto.user.ShoppingCartDTO
import cn.li.network.retrofit.datasource.RetrofitUserShopCartDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiUserShopCartRepository @Inject constructor(
    private val cartDataSource: RetrofitUserShopCartDataSource
) : UserShopCartRepository{
    override suspend fun addCommodityToShoppingCart(dto: ShoppingCartAddDTO): ApiResult<Nothing> {
        return cartDataSource.addCommodityToShoppingCart(dto)
    }

    override suspend fun clearShoppingCart(shopId: Long): ApiResult<Nothing> {
        return cartDataSource.clearShoppingCart(shopId)
    }

    override suspend fun getShoppingCartList(shopId: Long): ApiResult<List<ShoppingCartDTO>> {
        return cartDataSource.getShoppingCartList(shopId)
    }

    override suspend fun updateShoppingCartCommodityCount(
        ids: List<Long>,
        counts: List<Int>
    ): ApiResult<Nothing> {
        return cartDataSource.updateShoppingCartCommodityCount(
            ids = ids.joinToString(","),
            counts = counts.joinToString(",")
        )
    }
}