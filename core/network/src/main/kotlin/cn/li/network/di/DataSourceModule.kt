package cn.li.network.di

import cn.li.network.api.common.CommonDataSource
import cn.li.network.api.employee.EmployeeDataSource
import cn.li.network.api.user.UserAddressBookApi
import cn.li.network.api.user.UserAddressBookDataSource
import cn.li.network.api.user.UserDataSource
import cn.li.network.retrofit.datasource.RetrofitAddressDataSource
import cn.li.network.retrofit.datasource.RetrofitCommonDataSource
import cn.li.network.retrofit.datasource.RetrofitEmployeeDataSource
import cn.li.network.retrofit.datasource.RetrofitUserDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class DataSourceModule {

    @Binds
    abstract fun bindsRetrofitUserDataSource(
        retrofitUserDataSource: RetrofitUserDataSource
    ): UserDataSource


    @Binds
    abstract fun bindsRetrofitEmployeeDataSource(
        retrofitEmployeeDataSource: RetrofitEmployeeDataSource
    ): EmployeeDataSource


    @Binds
    abstract fun bindsRetrofitCommonDataSource(
        retrofitCommonDataSource: RetrofitCommonDataSource
    ): CommonDataSource


    @Binds
    abstract fun bindsRetrofitAddressDataSource(
        retrofitAddressDataSource: RetrofitAddressDataSource
    ): UserAddressBookDataSource
}