package cn.li.network.dto.user

/**
 * 购物车添加商品信息DTO
 * */
data class ShoppingCartAddDTO(
    val dishId: Long?,
    val setmealId: Long?,
    val number: Int,
    val shopId: Long,
)

/**
 * 购物车商品信息DTO
 * @param id
 * @param name 商品名称
 * @param image 图片url
 * @param userId 用户ID
 * @param dishId 商品id
 * @param selmealId 套餐id
 * @param dishFlavor 配置信息
 * @param number 数量
 * @param amount 丹姐
 * @param createTime 创建时间
 * @param shopId 商品id
 * */
data class ShoppingCartDTO(
    val id: Long,
    val name: String,
    val image: String,
    val userId: Long,
    val dishId: Long,
    val setmealId: Long,
    val dishFlavor: String?,
    val number: Int,
    val amount: Double,
    val createTime: String,
    val shopId: Long,
    val status: Int,
)
