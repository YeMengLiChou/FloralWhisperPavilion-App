package cn.li.data.repository

import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.ShoppingCartAddDTO
import cn.li.network.dto.user.ShoppingCartDTO

interface UserShopCartRepository {

    suspend fun addCommodityToShoppingCart(dto: ShoppingCartAddDTO): ApiResult<Nothing>

    suspend fun clearShoppingCart(shopId: Long): ApiResult<Nothing>

    suspend fun getShoppingCartList(shopId: Long): ApiResult<List<ShoppingCartDTO>>

    suspend fun updateShoppingCartCommodityCount(
        ids: List<Long>,
        counts: List<Int>
    ): ApiResult<Nothing>

}