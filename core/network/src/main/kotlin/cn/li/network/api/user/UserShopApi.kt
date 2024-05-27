package cn.li.network.api.user

import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.ShopGoodsDTO
import cn.li.network.dto.user.ShopItemDTO
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 店铺相关api
 * */
internal interface UserShopApi {


    /**
     * 获取门店列表
     * see: [文档](http://8.134.200.196:8080/doc.html#/user/%E5%BA%97%E9%93%BA%E7%9B%B8%E5%85%B3%E6%8E%A5%E5%8F%A3/getShopList)
     * */
    @GET("user/shop/getShopList")
    suspend fun getShopList(): ApiResult<List<ShopItemDTO>>


    /**
     *
     * see: [文档](http://8.134.200.196:8080/doc.html#/user/%E5%BA%97%E9%93%BA%E7%9B%B8%E5%85%B3%E6%8E%A5%E5%8F%A3/getStatus)
     * */
    @GET("user/shop/getStatus")
    suspend fun getStatus(@Query("shopId") shopId: Long): ApiResult<Int>

    @GET("user/shop/getShopItemList")
    suspend fun getShopGoodsItemList(@Query("shopId") shopId: Long): ApiResult<List<ShopGoodsDTO>>
}

/**
 * 店铺数据源
 * */
interface UserShopDataSource {
    suspend fun getShopList(): ApiResult<List<ShopItemDTO>>

    suspend fun getStatus(shopId: Long): ApiResult<Int>

    suspend fun getShopGoodsItemList(@Query("shopId") shopId: Long): ApiResult<List<ShopGoodsDTO>>

}