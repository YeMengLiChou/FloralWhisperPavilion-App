package cn.li.network.dto.user


/**
 * 商店信息
 * @param id 门店id
 * @param name 门店名称
 * @param address 门店地址
 * @param startTime 营业开始时间
 * @param endTime 营业结束时间
 * @param status 营业状态
 * */
data class ShopItemDTO(
    val id: Long,
    val name: String,
    val address: String,
    val startTime: String,
    val endTime: String,
    val status: Int,
) {
    companion object {
        /**
         * 营业中
         * */
        const val STATUS_IN_BUSINESS = 1

        /**
         * 打烊
         * */
        const val STATUS_NOT_IN_BUSINESS = 0
    }
}

/**
 * 门店中的所有商品信息
 * @param categoryId 分类id
 * @param categoryName 分类名称
 * @param categoryType 类别：1单品分类 2套餐分类
 * @param categoryStatus 分类状态:  0停用 1启用
 * @param items 商品列表
 * */
data class ShopGoodsDTO(
    val categoryId: Long,
    val categoryName: String,
    val categoryStatus: Int,
    val categoryType: Int,
    val sort: Int,
    val items: List<GoodsItem>
) {
    companion object {
        /**
         * 单品分类
         * */
        const val CATEGORY_TYPE_SINGLE = 1

        /**
         * 套餐分类
         * */
        const val CATEGORY_TYPE_SETMEAL = 2


        /**
         * 可售卖
         * */
        const val CATEGORY_STATUS_ENABLE = 1

        /**
         * 不可售卖，已售罄
         * */
        const val CATEGORY_STATUS_DISABLE = 0
    }
}

/**
 * 商品项
 * @param id
 * @param name
 * */
data class GoodsItem(
    val id: Long,
    val name: String,
    val image: String,
    val description: String,
    val price: Double,
    val status: Int,
    val isSetmeal: Int,
) {
    companion object {
        const val STATUS_ENABLE = 1
        const val STATUS_DISABLE = 0
        const val IS_SETMEAL_YES = 1
        const val IS_SETMEAL_NO = 0
    }
}