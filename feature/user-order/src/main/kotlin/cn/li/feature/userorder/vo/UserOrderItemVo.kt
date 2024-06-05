package cn.li.feature.userorder.vo

import cn.li.model.CommodityItemVO
import cn.li.network.dto.user.ShoppingCartDTO


/***
 * 订单列表的子项
 * */
data class UserOrderItemVo(
    val orderId: Long,
    val orderTotalAmount: String,
    val orderNumber: String,
    val shopName: String,
    val shopAddress: String,
    val status: Int,
    val statusText: String,
    val completedOrderTime: String?,
    val submitOrderTime: String,
    val commodityCount: Int,
    val commodityList: List<CommodityItemVO>
)

fun List<CommodityItemVO>.toCartInfo(): List<ShoppingCartDTO> {
    return map {
        ShoppingCartDTO(
            id = it.id,
            name = it.name,
            image = it.imageUrl,
            number = it.cartCount,
            amount = it.price,
            shopId = 0L,
            dishId = 0L,
            setmealId = 0L,
            dishFlavor = "",
            status = 0,
            userId = 0L,
            createTime = "",
        )
    }
}