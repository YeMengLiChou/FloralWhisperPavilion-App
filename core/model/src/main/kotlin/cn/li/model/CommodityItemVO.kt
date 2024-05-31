package cn.li.model

data class CommodityItemVO(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val description: String,
    val price: Double,
    val cartCount: Int,
    val status: Int,
    val cartItemId: Long?,
    val isSetmeal: Int,
    )


data class ShopCommodityItemVO(
    val categoryId: Long,
    val categoryName: String,
    val categoryStatus: Int,
    val categoryType: Int,
    val sort: Int,
    val items: List<CommodityItemVO>
)



data class CommodityItemDetailVO(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val description: String,
    val price: Double,
    val status: Int,
    val isSetmeal: Boolean
)