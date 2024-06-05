package cn.li.feature.userorder.vo

import cn.li.model.CommodityItemVO


/***
 * 订单列表的子项
 * */
data class UserOrderItemVo(
    val orderId: Long,
    val orderTotalAmount: String,
    val orderNumber: String,
    val shopName: String,
    val shopAddress: String,
    val statusText: String,
    val completedOrderTime: String?,
    val submitOrderTime: String,
    val commodityCount: Int,
    val commodityList: List<CommodityItemVO>
)
