package cn.li.network.dto.user

/**
 * 订单详情 DTO
 * @param id 订单id
 * @param number 订单好
 * @param status 订单状态
 * @param userId 下单用户id
 * @param shopId 下单商店id
 * @param addressBookId 订单地址id
 * @param orderTime 下单事件
 * @param checkoutTime 付款时间
 * @param payStatus 下单状态
 * @param amount 付款金额
 * @param remark 备注(nullable)
 * @param phone 手机号
 * @param address 收货地址
 * @param userName 用户名称
 * @param consignee 收货人
 * @param cancelReason 订单取消原因
 * @param rejectionReason 订单拒绝理由
 * @param cancelTime 订单取消时间
 * @param estimateDeliveryTime 预计送达时间
 * @param deliveryStatus 配送状态
 * @param deliveryTime 送达时间
 * @param packAmount 打包费
 * @param orderDishes 订单菜品信息
 * @param orderDetailList 订单详情
 * */
data class OrderDetailDTO(
    val id: Long,
    val number: String,
    val status: Int,
    val userId: Long,
    val shopId: Long,
    val addressBookId: Long,
    val orderTime: String,
    val checkoutTime: String?,
    val payStatus: Int,
    val amount: Double,
    val remark: String?,
    val phone: String?,
    val address: String?,
    val userName: String?,
    val consignee: String?,
    val cancelReason: String?,
    val rejectionReason: String?,
    val cancelTime: String?,
    val estimatedDeliveryTime: String?,
    val deliveryStatus: Int,
    val deliveryTime: String?,
    val packAmount: Double,
)

data class UserInfoDTO(
    val id: Long,
    val username: String,
    val nickname: String,
    val password: String,
    val phone: String,
    val sex: Int,
    val avatar: String,
    val createTime: String,
    val updateTime: String,
    val status: Boolean,
)

data class OrderShopDTO(
    val id: Long,
    val name: String,
    val address: String,
    val owner: String,
    val startTime: String,
    val endTime: String,
    val status: Int,
    val phone: String
)



/**
 * 订单详情
 * @param id
 * @param name
 * @param image
 * @param
 * */
data class ItemDetail(
    val id: Long,
    val name: String,
    val image: String,
    val orderId: Long,
    val shopId: Long,
    val shopName: String?,
    val dishId: Long,
    val setmealId: Long,
    val dishFlavor: String?,
    val number: Int,
    val amount: Double,
    val dish: CommodityDetailDTO
)

data class OrderRecordDTO(
    val orders: OrderDetailDTO,
    val user: UserInfoDTO,
    val shop: OrderShopDTO,
    val addressBook: AddressBookDTO?,
    val orderDetailList: List<ItemDetail>
)
