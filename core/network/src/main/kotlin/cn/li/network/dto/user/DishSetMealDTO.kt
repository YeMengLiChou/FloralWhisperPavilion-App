package cn.li.network.dto.user

/**
 * 套餐商品信息
 * @param name 名称
 * @param copies TODO: unknown
 * @param image 图片url
 * @param description 描述
 * */
data class DishSetMealDTO(
    val name: String,
    val copies: Int,
    val image: String,
    val description: String,
)

/**
 * 套餐说明
 * @param id 主键
 * @param name 套餐名
 * @param categoryId 分类id
 * @param price 套餐价格
 * @param image 图片url
 * @param description 套餐描述
 * @param status 售卖状态
 * @param createTime 创建时间
 * @param updateTime 更新时间
 * @param updateUserId 更新人id
 * @param createUser 创建人id
 * @param shopId 商店id
 * */
data class SetMealDTO(
    val id: Long,
    val name: String,
    val categoryId: Long,
    val price: Double,
    val image: String,
    val description: String,
    val status: Short,
    val createTime: String,
    val updateTime: String,
    val createUser: Long,
    val updateUserId: Long,
    val shopId: Long,
)

/**
 * 商品信息
 * @param dishId 商品id
 * @param dishName 商品名称
 * @param categoryId 分类id
 * @param price 价格
 * @param image 图片 url
 * @param description 描述信息
 * @param status 状态（0停售1售卖）
 * @param updateTime 更细时间
 * @param categoryName 分类名称
 * */
data class DishDTO(
    val dishId: Long,
    val dishName: String,
    val categoryId: Long,
    val price: Double,
    val image: String,
    val description: String,
    val status: Short,
    val updateTime: String,
    val categoryName: String
)

