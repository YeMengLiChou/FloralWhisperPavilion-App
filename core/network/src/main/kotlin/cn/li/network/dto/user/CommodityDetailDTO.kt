package cn.li.network.dto.user

data class CommodityDetailDTO(
    val dishId: Long,
    val dishName: String,
    val categoryId: Long,
    val price: Double,
    val image: String,
    val description: String,
    val status: Int,
    val createTime: String,
    val updateTime: String,
    val createUser: Long,
    val updateUser: Long,
    val shopId: Long
)
