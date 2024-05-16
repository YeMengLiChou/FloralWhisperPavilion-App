package cn.li.network.dto

/**
 * Api 分页结果
 * @param total 总记录条数
 * @param pageNo 当前页码
 * @param pageSize 总页数
 * @param size 当前页的数量
 * */
data class ApiPagination<T>(
    val total: Long,
    val pageSize: Long,
    val pageNo: Long,
    val size: Long,
    val records: List<T>
)


