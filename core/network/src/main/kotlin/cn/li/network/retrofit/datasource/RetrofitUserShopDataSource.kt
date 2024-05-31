package cn.li.network.retrofit.datasource

import cn.li.network.api.user.UserShopApi
import cn.li.network.api.user.UserShopDataSource
import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.ShopGoodsDTO
import cn.li.network.dto.user.ShopItemDTO
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitUserShopDataSource @Inject constructor(
    retrofit: Retrofit
) : UserShopDataSource {

    private val userShopApi = retrofit.create(UserShopApi::class.java)
    override suspend fun getShopList(): ApiResult<List<ShopItemDTO>> {
        return userShopApi.getShopList()
    }

    override suspend fun getStatus(shopId: Long): ApiResult<Int> {
        return userShopApi.getStatus(shopId)
    }

    override suspend fun getShopGoodsItemList(shopId: Long): ApiResult<List<ShopGoodsDTO>> {
        return userShopApi.getShopGoodsItemList(shopId)
    }
}