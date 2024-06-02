package cn.li.network.dto.user

/**
 * 订单提交响应结果 DTO
 * @param id 订单id
 * @param orderNumber 订单号
 * @param orderAmount 订单金额
 * @param orderTime 下单时间
 * */
data class OrderSubmitResultDTO(
    val id: Long,
    val orderNumber: String,
    val orderAmount: Double,
    val orderTime: String,

)


/**
 * 订单提交 DTO
 * @param addressBookId 地址簿id
 * @param shopId 商店id
 * @param remark 备注
 * @param estimatedDeliveryTime 预计送达时间
 * @param deliveryStatus 配送黄台
 * @param packAmount 配送费
 * @param amount 下单金额
 * */
data class OrderSubmitDTO(
    val addressBookId: Long?,
    val shopId: Long,
    val remark: String?,
    val estimatedDeliveryTime: String?,
    val deliveryStatus: Int,
    val packAmount: String,
    val amount: String
) {
    companion object {
        /**
         * 自取
         * */
        const val DELIVERY_STATUS_SELF_PICKUP = 2

        /**
         * 立即送出
         * */
        const val DELIVERY_STATUS_DELIVERY = 1
    }
}

