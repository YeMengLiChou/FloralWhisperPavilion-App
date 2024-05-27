package cn.li.data.repository

import cn.li.network.api.user.UserShopDataSource
import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.ShopGoodsDTO
import cn.li.network.dto.user.ShopItemDTO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiShopRepository @Inject constructor(
    private val shopDataSource: UserShopDataSource
) : ShopRepository {
    override suspend fun getShopList(): ApiResult<List<ShopItemDTO>> {
        return shopDataSource.getShopList()
    }

    override suspend fun getStatus(shopId: Long): ApiResult<Int> {
        return shopDataSource.getStatus(shopId)
    }


    override suspend fun getGoodsItemList(shopId: Long): ApiResult<List<ShopGoodsDTO>> {
        return shopDataSource.getShopGoodsItemList(shopId)
    }
}