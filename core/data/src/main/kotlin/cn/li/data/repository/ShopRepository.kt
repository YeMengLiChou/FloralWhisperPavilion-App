package cn.li.data.repository

import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.ShopGoodsDTO
import cn.li.network.dto.user.ShopItemDTO

/**
 * 店铺的数据仓库
 * */
interface ShopRepository {

    suspend fun getShopList(): ApiResult<List<ShopItemDTO>>

    suspend fun getStatus(shopId: Long): ApiResult<Int>

    suspend fun getGoodsItemList(shopId: Long): ApiResult<List<ShopGoodsDTO>>
}