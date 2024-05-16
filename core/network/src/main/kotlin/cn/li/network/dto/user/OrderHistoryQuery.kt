package cn.li.network.dto.user

/**
 * 查询历史数据的参数
 * @param number 订单号（模糊匹配）
 * @param beginTime 下单起始时间（yyyy-MM-dd）
 * @param endTime 下单结束时间（yyyy-MM-dd）
 * @param status 订单状态
 * @param payStatus 支付状态
 * @param shopId 店铺Id
 * @param pageNo 页码（默认为1）
 * @param pageSize 每页数量（默认为5）
 * @param sortBy 排序字段（下划线命名）
 * @param isAsc 升序排列（默认为true）
 * */
data class OrderHistoryQuery(
    val number: String? = null,
    val beginTime: String? = null,
    val endTime: String? = null,
    val status: String? = null,
    val payStatus: Int? = null,
    val shopId: Long? = null,
    val pageNo: Int = 1,
    val pageSize: Int = 5,
    val sortBy: String? = null,
    val isAsc: Boolean = true
) {
    /**
     * 转为请求所需的 QueryMap
     * */
    fun toQueryMap(): Map<String, Any> {
        return HashMap<String, Any>().apply {
            number?.let { put("number", it) }
            beginTime?.let { put("beginTime", it) }
            endTime?.let { put("endTime", it) }
            status?.let { put("status", it) }
            payStatus?.let { put("payStatus", it) }
            shopId?.let { put("shopId", it) }
            sortBy?.let { put("sortBy", it) }
            put("number", pageNo)
            put("number", pageSize)
            put("number", isAsc)

        }
    }
}