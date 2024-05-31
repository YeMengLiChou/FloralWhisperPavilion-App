package cn.li.feature.userorder.vo

import cn.li.network.dto.user.ShoppingCartDTO

/**
 * 下单界面应该展示的数据
 * @param shopId 商店 id
 * @param shopName 商店名称
 * @param shopLocation 商店地址
 * */
data class UserOrderSettlementVo(
    val shopId: Long,
    val shopName: String,
    val shopLocation: String,
    val deliveryStatus: Int,
    val deliveryAddress: String?,
    val orderAmount: Double,
    val cartDTO: List<ShoppingCartDTO>
) {
    companion object {
        const val DELIVERY_STATUS_SELF_PICKUP = 2
        const val DELIVERY_STATUS_DELIVERY = 1
    }
}
