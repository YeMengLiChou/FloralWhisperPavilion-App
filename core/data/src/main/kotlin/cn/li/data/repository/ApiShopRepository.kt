package cn.li.data.repository

import cn.li.network.api.user.UserCommodityDataSource
import cn.li.network.api.user.UserShopDataSource
import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.CommodityDetailDTO
import cn.li.network.dto.user.ShopGoodsDTO
import cn.li.network.dto.user.ShopItemDTO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiShopRepository @Inject constructor(
    private val shopDataSource: UserShopDataSource,
    private val commodityDataSource: UserCommodityDataSource
) : ShopRepository {
    override suspend fun getShopList(): ApiResult<List<ShopItemDTO>> {
        return shopDataSource.getShopList()
    }

    override suspend fun getShopStatus(shopId: Long): ApiResult<Int> {
        return shopDataSource.getStatus(shopId)
    }


    override suspend fun getCommodityItemList(shopId: Long): ApiResult<List<ShopGoodsDTO>> {
        return shopDataSource.getShopGoodsItemList(shopId)
    }

    override suspend fun getCommodityDetailById(commodityId: Long): ApiResult<CommodityDetailDTO> {
        return commodityDataSource.getDetailById(commodityId)
    }
}