package cn.li.network.retrofit.datasource

import cn.li.network.api.user.UserAddressBookApi
import cn.li.network.api.user.UserAddressBookDataSource
import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.AddressBookAddDTO
import cn.li.network.dto.user.AddressBookDTO
import cn.li.network.dto.user.AddressBookUpdateDTO
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RetrofitAddressDataSource @Inject constructor(
    retrofit: Retrofit
) : UserAddressBookDataSource {

    private val addressApi = retrofit.create<UserAddressBookApi>()

    override suspend fun addNewAddress(dto: AddressBookAddDTO): ApiResult<Nothing> {
        return addressApi.addNewAddress(dto)
    }

    override suspend fun updateAddress(dto: AddressBookUpdateDTO): ApiResult<Nothing> {
        return addressApi.updateAddress(dto)
    }

    override suspend fun getAddressBookById(id: Long): ApiResult<AddressBookAddDTO> {
        return addressApi.getAddressBookById(id)
    }

    override suspend fun deleteAddressBooksByIds(ids: List<Long>): ApiResult<Nothing> {
        return addressApi.deleteAddressBooksByIds(ids)
    }

    override suspend fun getDefaultAddressBook(): ApiResult<AddressBookDTO> {
        return addressApi.getDefaultAddressBook()
    }

    override suspend fun setDefaultAddressBook(id: Long): ApiResult<Nothing> {
        return addressApi.setDefaultAddressBook(id)
    }

    override suspend fun getUserAddressBooksList(): ApiResult<List<AddressBookDTO>> {
        return addressApi.getUserAddressBooksList()
    }
}