package cn.li.data.repository


import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.AddressBookAddDTO
import cn.li.network.dto.user.AddressBookDTO
import cn.li.network.dto.user.AddressBookUpdateDTO

interface AddressRepository {

    suspend fun addNewAddress(dto: AddressBookAddDTO): ApiResult<Nothing>

    suspend fun updateAddress(dto: AddressBookUpdateDTO): ApiResult<Nothing>

    suspend fun getAddressBookById(id: Long): ApiResult<AddressBookAddDTO>

    suspend fun deleteAddressBooksByIds(ids: List<Long>): ApiResult<Nothing>

    suspend fun getDefaultAddressBook(): ApiResult<AddressBookDTO>


    suspend fun setDefaultAddressBook(id: Long): ApiResult<Nothing>

    suspend fun getUserAddressBooksList(): ApiResult<List<AddressBookDTO>>

}

