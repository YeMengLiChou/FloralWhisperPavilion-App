package cn.li.data.repository

import cn.li.network.api.user.UserAddressBookDataSource
import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.AddressBookAddDTO
import cn.li.network.dto.user.AddressBookDTO
import cn.li.network.dto.user.AddressBookUpdateDTO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiAddressRepository @Inject constructor(
    private val retrofitDataSource: UserAddressBookDataSource
) : AddressRepository {
    override suspend fun addNewAddress(dto: AddressBookAddDTO): ApiResult<Nothing> {
        return retrofitDataSource.addNewAddress(dto)
    }

    override suspend fun updateAddress(dto: AddressBookUpdateDTO): ApiResult<Nothing> {
        return retrofitDataSource.updateAddress(dto)
    }

    override suspend fun getAddressBookById(id: Long): ApiResult<AddressBookAddDTO> {
        return retrofitDataSource.getAddressBookById(id)
    }

    override suspend fun deleteAddressBooksByIds(ids: List<Long>): ApiResult<Nothing> {
        return retrofitDataSource.deleteAddressBooksByIds(ids)
    }

    override suspend fun getDefaultAddressBook(): ApiResult<AddressBookDTO> {
        return retrofitDataSource.getDefaultAddressBook()
    }

    override suspend fun setDefaultAddressBook(id: Long): ApiResult<Nothing> {
        return retrofitDataSource.setDefaultAddressBook(id)
    }

    override suspend fun getUserAddressBooksList(): ApiResult<List<AddressBookDTO>> {
        return retrofitDataSource.getUserAddressBooksList()
    }

}