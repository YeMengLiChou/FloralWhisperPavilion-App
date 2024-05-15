package cn.li.core.dto.user

/**
 * 类别DTO
 * @param id
 * @param name 名称
 * @param type 分诶类型（1菜品分类 2套餐分类）
 * @param sort 排序编号
 * @param status 状态（0禁用 1启用）
 * @param createTime 创建时间
 * @param updateTime 更新时间
 * @param createUser 创建人id
 * @param updateUser 更新人id
 * @param shopId 商店id
 * */
data class CategoryDTO(
    val id: Long,
    val name: String,
    val type: Short,
    val sort: Int,
    val status: Short,
    val createTime: String,
    val updateTime: String,
    val createUser: Long,
    val updateUser: Long,
    val shopId: Long,
)