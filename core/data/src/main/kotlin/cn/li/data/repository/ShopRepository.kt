package cn.li.data.repository

import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.CommodityDetailDTO
import cn.li.network.dto.user.ShopGoodsDTO
import cn.li.network.dto.user.ShopItemDTO

/**
 * 店铺的数据仓库
 * */
interface ShopRepository {

    suspend fun getShopList(): ApiResult<List<ShopItemDTO>>

    suspend fun getShopStatus(shopId: Long): ApiResult<Int>

    suspend fun getCommodityItemList(shopId: Long): ApiResult<List<ShopGoodsDTO>>

    suspend fun getCommodityDetailById(commodityId: Long): ApiResult<CommodityDetailDTO>

}