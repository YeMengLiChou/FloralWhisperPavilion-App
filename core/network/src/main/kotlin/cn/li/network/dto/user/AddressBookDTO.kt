package cn.li.network.dto.user

import kotlin.reflect.jvm.internal.impl.util.ModuleVisibilityHelper.EMPTY

/**
 * 地址簿新增DTO
 * @param consignee 收货人
 * @param sex 性别 0女-1男
 * @param phone 电话号码
 * @param provinceCode 省级划分编号
 * @param provinceName 省级名称(nullable)
 * @param cityCode 市级划分编号(nullable)
 * @param cityName 市级名称(nullable)
 * @param districtCode 区级划分编号(nullable)
 * @param districtName 区级名称(nullable)
 * @param label 标签(nullable)
 * */
data class AddressBookAddDTO(
    val consignee: String,
    val sex: Short,
    val phone: String,
    val provinceCode: String?,
    val provinceName: String?,
    val cityCode: String?,
    val cityName: String?,
    val districtCode: String?,
    val districtName: String?,
    val detail: String,
    val label: String?
) {
    companion object {
        val EMPTY = AddressBookAddDTO(
            consignee = "",
            sex = -1,
            phone = "",
            provinceCode = null,
            provinceName = null,
            cityCode = null,
            cityName = null,
            districtCode =  null,
            districtName = null,
            label = null,
            detail = "",
        )
    }
}

/**
 * 地址簿更新DTO
 * @param id 更新的地址簿id
 * @param consignee 收货人
 * @param sex 性别 0女-1男
 * @param phone 电话号码
 * @param provinceCode 省级划分编号
 * @param provinceName 省级名称(nullable)
 * @param cityCode 市级划分编号(nullable)
 * @param cityName 市级名称(nullable)
 * @param districtCode 区级划分编号(nullable)
 * @param districtName 区级名称(nullable)
 * @param label 标签(nullable)
 * */
data class AddressBookUpdateDTO(
    val id: Long,
    val consignee: String,
    val sex: Short,
    val phone: String,
    val provinceCode: String?,
    val provinceName: String?,
    val cityCode: String?,
    val cityName: String?,
    val districtCode: String?,
    val districtName: String?,
    val detail: String,
    val label: String?
)

/**
 * 地址信息DTO
 * @param id 更新的地址簿id
 * @param userId 所属用户id
 * @param consignee 收货人
 * @param sex 性别 0女-1男
 * @param phone 电话号码
 * @param provinceCode 省级划分编号
 * @param provinceName 省级名称(nullable)
 * @param cityCode 市级划分编号(nullable)
 * @param cityName 市级名称(nullable)
 * @param districtCode 区级划分编号(nullable)
 * @param districtName 区级名称(nullable)
 * @param label 标签(nullable)
 * @param isDefault 是否为默认 0否1是
 * */
data class AddressBookDTO(
    val id: Long,
    val userId: Long,
    val consignee: String,
    val sex: Short,
    val phone: String,
    val provinceCode: String?,
    val provinceName: String?,
    val cityCode: String?,
    val cityName: String?,
    val districtCode: String?,
    val districtName: String?,
    val detail: String,
    val label: String?,
    val isDefault: Short,
)

/**
 * 转为 [AddressBookUpdateDTO]
 * */
fun AddressBookDTO.toUpdateDTO(): AddressBookUpdateDTO {
    return AddressBookUpdateDTO(
        id = id,
        consignee = consignee,
        sex = sex,
        phone = phone,
        provinceName = provinceName,
        provinceCode = provinceCode,
        cityName = cityName,
        cityCode = cityCode,
        districtName = districtName,
        districtCode = districtCode,
        label = label,
        detail = detail
    )
}